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
import net.thumbtack.school.buscompany.exception.CheckedException;
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


    public ResponseEntity<RegisterClientDtoResponse> registerClient(RegisterClientDtoRequest request) {
        Client client = ClientMapstructMapper.INSTANCE.fromRegisterDto(request);
        client.setUserType(UserType.CLIENT);

        String uuid = userDao.register(client);

        ResponseCookie cookie = createJavaSessionIdCookie(uuid);

        LOGGER.info("User-" + client.getUserType() + " " + client.getLogin() + " just registered");
        RegisterClientDtoResponse response = ClientMapstructMapper.INSTANCE.toRegisterDto(client);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public UpdateUserProfileDtoResponse updateClientProfile(String cookieValue, UpdateClientProfileDtoRequest request) {
        Client client = getClient(cookieValue);

        if(!client.getPassword().equals(request.getOldPassword())){
            throw new CheckedException(ErrorCode.INVALID_PASSWORD);
        }

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPassword(request.getNewPassword());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        LOGGER.info("User-" + client.getUserType() + " " + client.getLogin() + " just updated his profile");
        userDao.updateUser(client);

        return ClientMapstructMapper.INSTANCE.toUpdateDto(client);
    }

    public OrderTicketDtoResponse orderTrip(String cookieValue, OrderTicketDtoRequest request) {
        Client client = getClient(cookieValue);

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
            throw new CheckedException(ErrorCode.TRIP_NOT_EXISTS);
        }

        if(!trip.isApproved()){
            throw new CheckedException(ErrorCode.TRIP_NOT_APPROVED);
        }

        order.setTrip(trip);

        if(!inBetween(order.getDate(), trip.getTripDates())){
            throw new CheckedException(ErrorCode.NO_TRIP_ON_THIS_DATE);
        }

        if(clientDao.takeSeats(order) == 0){
            throw new CheckedException(ErrorCode.NO_FREE_PLACES);
        }

        clientDao.insertOrder(order);

        Place place = new Place();
        place.setTripId(clientDao.getIdTripDate(order));
        place.setOrder(order);

        for(Passenger passenger : order.getPassengers()){
            place.setPassenger(passenger);

            List<Integer> freeSeats = clientDao.getFreeSeats(order);
            Iterator<Integer> seatIterator = freeSeats.listIterator();

            do {
                place.setPlaceNumber(seatIterator.next());
                LOGGER.info("User-" + client.getUserType() + " " + client.getLogin()
                        + " register passenger on trip " + trip.getFromStation() + " to " + trip.getToStation() +
                " on date " + order.getDate() + " on place " + place.getPlaceNumber());
            } while(clientDao.takeSeat(place) == 0);
        }

        OrderTicketDtoResponse response = OrderMapstructMapper.INSTANCE.toDtoResponse(order);
        List<PassengerDtoResponse> passengerDtoResponses = new ArrayList<>();

        for(Passenger passenger : order.getPassengers()){
            PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
            passengerDtoResponses.add(passengerDtoResponse);
        }
        response.setPassengers(passengerDtoResponses);
        response.setTotalPrice(order.getPassengersNumber() * order.getTrip().getPrice());

        LOGGER.info("User-" + client.getUserType() + " " + client.getLogin() + " ordered the trip from " + trip.getFromStation() + " to " + trip.getToStation());
        return response;
    }

    public CancelOrderDtoResponse cancelOrder(String cookieValue, String orderId) {
        Client client = getClient(cookieValue);

        int idOrder = Integer.parseInt(orderId);

        Order order = userDao.getOrderById(idOrder);

        if(order == null){
            throw new CheckedException(ErrorCode.ORDER_NOT_EXISTS);
        }

        if(order.getClient().getId() != client.getId()) {
            throw new CheckedException(ErrorCode.NOT_CLIENT_ORDER);
        }

        clientDao.cancelOrder(idOrder);

        LOGGER.info("User-" + client.getUserType() + " " + client.getLogin() + " canceled his order to trip from "
                + order.getTrip().getFromStation() + " to " + order.getTrip().getToStation());
        return new CancelOrderDtoResponse();
    }

    public GetFreePlacesDtoResponse getFreePlaces(String cookieValue, String orderId) {
        try{
            Integer.parseInt(orderId);
        }
        catch (NumberFormatException ex){
            throw new CheckedException(ErrorCode.INVALID_ID);
        }

        getClient(cookieValue);

        Order order = userDao.getOrderById(Integer.parseInt(orderId));

        if(order == null){
            throw new CheckedException(ErrorCode.ORDER_NOT_EXISTS);
        }

        List<Integer> freeSeats = clientDao.getFreeSeats(order);

        return new GetFreePlacesDtoResponse(freeSeats);
    }

    public ChooseSeatDtoResponse choosePlace(String cookieValue, ChooseSeatDtoRequest request) {
        getClient(cookieValue);

        Order order = userDao.getOrderById(request.getOrderId());

        if(order == null){
            throw new CheckedException(ErrorCode.ORDER_NOT_EXISTS);
        }

        Passenger passenger = order.getPassenger(request.getFirstName(), request.getLastName(), request.getPassport());

        if(passenger == null){
            passenger = OrderMapstructMapper.INSTANCE.fromDtoRequestToPassenger(request);
            order.addPassenger(passenger);
            clientDao.insertPassenger(order, passenger);
        }

        Place place = new Place();
        place.setPlaceNumber(request.getPlace());
        place.setOrder(order);
        place.setTripId(order.getTrip().getId());

        if(order.containsPassenger(passenger)){
            place.setPassenger(passenger);
            clientDao.changeSeat(place);
        }

        String ticket = "Билет " + request.getOrderId() + "_" + request.getPlace();

        ChooseSeatDtoResponse response = OrderMapstructMapper.INSTANCE.fromDtoRequest(request);
        response.setTicket(ticket);

        LOGGER.info("User chose seat for passenger " + passenger.getLastName() + " at the trip from "
                + order.getTrip().getFromStation() + " to " + order.getTrip().getToStation());

        return response;
    }
}
