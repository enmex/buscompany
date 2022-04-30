package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
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

import java.util.*;

@Service
public class ClientService extends ServiceBase{
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    public ClientService(UserDao userDao, AdminDao adminDao, ClientDao clientDao) {
        super(userDao, adminDao, clientDao);
    }


    public ResponseEntity<RegisterClientDtoResponse> registerClient(String cookieValue, RegisterClientDtoRequest request) {
        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION, "registerClient");
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

        LOGGER.info("User-" + client.getUserType() + " " + client.getLogin() + " just registered");
        RegisterClientDtoResponse response = ClientMapstructMapper.INSTANCE.toRegisterDto(client);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public UpdateUserProfileDtoResponse updateClientProfile(String cookieValue, UpdateClientProfileDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "updateClientProfile");
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

        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " just updated his profile");
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
        order.setPassengers(passengers);
        order.setClient(client);

        Trip trip = userDao.getTripById(request.getTripId());

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS, "orderTicket");
        }

        if(!trip.isApproved()){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_APPROVED, "orderTicket");
        }

        order.setTrip(trip);

        if(!inBetween(order.getDate(), trip.getDates())){
            throw new BusCompanyException(ErrorCode.NO_TRIP_ON_THIS_DATE, "orderTicket");
        }

        clientDao.insertOrder(order);

        List<Integer> freeSeats = clientDao.getFreeSeats(order);

        for(Passenger passenger : order.getPassengers()){
            Place place = new Place();

            place.setPlaceNumber(freeSeats.get(0));
            freeSeats.remove(0);

            place.setOrder(order);
            place.setPassenger(passenger);

            clientDao.takeSeat(place);
        }

        OrderTicketDtoResponse response = OrderMapstructMapper.INSTANCE.toDtoResponse(order);
        List<PassengerDtoResponse> passengerDtoResponses = new ArrayList<>();

        for(Passenger passenger : order.getPassengers()){
            PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
            passengerDtoResponses.add(passengerDtoResponse);
        }
        response.setPassengers(passengerDtoResponses);
        response.setTotalPrice(order.getPassengersNumber() * order.getTrip().getPrice());

        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " ordered the trip from " + trip.getFromStation() + " to " + trip.getToStation());
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

        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " canceled his order to trip from " + order.getTrip().getFromStation() + " to " + order.getTrip().getToStation());
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

        List<Integer> freeSeats = clientDao.getFreeSeats(order);

        return new GetFreePlacesDtoResponse(freeSeats);
    }

    public ChooseSeatDtoResponse choosePlace(String cookieValue, ChooseSeatDtoRequest request) {
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "chooseSeat");
        }

        Order order = userDao.getOrderById(request.getOrderId());

        if(order == null){
            throw new BusCompanyException(ErrorCode.ORDER_NOT_EXISTS, "choosePlace");
        }

        Passenger passenger = order.getPassenger(request.getFirstName(), request.getLastName(), request.getPassport());

        if(passenger == null){
            passenger = OrderMapstructMapper.INSTANCE.fromDtoRequestToPassenger(request);
            order.addPassenger(passenger);
            clientDao.insertPassenger(order, passenger);
        }

        if(order.containsPassenger(passenger)){
            clientDao.changeSeat(passenger, request.getPlace());
        }

        String ticket = "Билет " + request.getOrderId() + "_" + request.getPlace();

        ChooseSeatDtoResponse response = OrderMapstructMapper.INSTANCE.fromDtoRequest(request);
        response.setTicket(ticket);

        LOGGER.info("User chose seat for passenger " + passenger.getLastName() + " at the trip from "
                + order.getTrip().getFromStation() + " to " + order.getTrip().getToStation());

        return response;
    }
}
