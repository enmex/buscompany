package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BaseDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateClientProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.*;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.ClientMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.OrderMapstructMapper;
import net.thumbtack.school.buscompany.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class ClientService extends ServiceBase{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    public ClientService(UserDao userDao, AdminDao adminDao, ClientDao clientDao) {
        super(userDao, adminDao, clientDao);
    }


    public ResponseEntity<RegisterClientDtoResponse> registerClient(String cookieValue, RegisterClientDtoRequest request) {
        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION);
        }

        Client client = ClientMapstructMapper.INSTANCE.fromRegisterDto(request);
        client.setUserType("client");

        String uuid = userDao.register(client);

        ResponseCookie cookie = createJavaSessionIdCookie(uuid);

        new Thread(() -> {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    userDao.closeSession(uuid);
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, cookieMaxAge * 1000);
        }).start();

        RegisterClientDtoResponse response = ClientMapstructMapper.INSTANCE.toRegisterDto(client);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public UpdateUserProfileDtoResponse updateClientProfile(String cookieValue, UpdateClientProfileDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("client")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "updateClientProfile");
        }

        Client client = (Client) user;

        if(!client.getPassword().equals(request.getOldPassword())){
            throw new BusCompanyException(ErrorCode.INVALID_PASSWORD, "password");
        }

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPassword(request.getNewPassword());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        userDao.updateUser(client);

        return ClientMapstructMapper.INSTANCE.toUpdateDto(client);
    }

    public OrderTicketDtoResponse orderTicket(String cookieValue, OrderTicketDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "orderTicket");
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "orderTicket");
        }

        Client client = (Client) user;

        Order order = OrderMapstructMapper.INSTANCE.fromDtoRequest(request);
        List<Passenger> passengers = new ArrayList<>();
        for(PassengerDtoRequest passengerDtoRequest : request.getPassengers()){
            Passenger passenger = OrderMapstructMapper.INSTANCE.fromDtoRequest(passengerDtoRequest);
            passengers.add(passenger);
        }

        clientDao.insertOrder(client.getId(), order);

        Trip trip = userDao.getTripById(request.getTripId());

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS, "orderTicket");
        }

        order.setTrip(trip);
        order.setClient(client);

        List<Integer> occupiedSeats = clientDao.getOccupiedSeats(trip.getId());
        Bus bus = userDao.getBus(trip.getBusName());
        int placeCount = Integer.parseInt(bus.getPlaceCount());

        List<Integer> freeSeats = new ArrayList<>();
        for(int placeNumber = 0; placeNumber < placeCount; placeNumber++){
            if(!occupiedSeats.contains(placeNumber)){
                freeSeats.add(placeNumber);
            }
        }

        for(int i = 0; i < order.getPassengersNumber(); i++){
            clientDao.takeSeat(order, order.getPassenger(i), freeSeats.get(i));
        }

        OrderTicketDtoResponse response = OrderMapstructMapper.INSTANCE.toDtoResponse(order);
        List<PassengerDtoResponse> passengerDtoResponses = new ArrayList<>();

        for(Passenger passenger : order.getPassengers()){
            PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
            passengerDtoResponses.add(passengerDtoResponse);
        }
        response.setPassengers(passengerDtoResponses);

        return response;
    }

    public CancelOrderDtoResponse cancelOrder(String cookieValue, String orderId) {
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "cancelOrder");
        }
        int idOrder = Integer.parseInt(orderId);

        Order order = userDao.getOrderById(idOrder);

        if(order == null){
            throw new BusCompanyException(ErrorCode.ORDER_NOT_EXISTS, "order");
        }

        clientDao.cancelOrder(idOrder);

        return new CancelOrderDtoResponse();
    }

    public GetFreePlacesDtoResponse getFreePlaces(String cookieValue, String orderId) {
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getFreePlaces");
        }

        Order order = userDao.getOrderById(Integer.parseInt(orderId));

        if(order == null){
            throw new BusCompanyException(ErrorCode.ORDER_NOT_EXISTS, "order");
        }

        Trip trip = order.getTrip();

        List<Integer> occupiedSeats = clientDao.getOccupiedSeats(Integer.parseInt(orderId));
        Bus bus = userDao.getBus(trip.getBusName());
        int placeCount = Integer.parseInt(bus.getPlaceCount());

        List<Integer> freeSeats = new ArrayList<>();
        for(int placeNumber = 0; placeNumber < placeCount; placeNumber++){
            if(!occupiedSeats.contains(placeNumber)){
                freeSeats.add(placeNumber);
            }
        }

        return new GetFreePlacesDtoResponse(freeSeats);
    }

    public ChooseSeatDtoResponse choosePlace(String cookieValue, ChooseSeatDtoRequest request) {
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "chooseSeat");
        }

        Passenger passenger = clientDao.getByPassport(request.getPassport());
        Order order = userDao.getOrderById(request.getOrderId());

        if(order.containsPassenger(passenger)){
            clientDao.changeSeat(passenger, request.getPlace());
        }

        String ticket = "Билет " + request.getOrderId() + "_" + request.getPlace();

        ChooseSeatDtoResponse response = OrderMapstructMapper.INSTANCE.fromDtoRequest(request);
        response.setTicket(ticket);

        return response;
    }
}
