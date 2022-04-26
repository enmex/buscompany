package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.PassengerDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrdersDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.OrderMapstructMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.service.DateService;
import net.thumbtack.school.buscompany.validation.Date;
import net.thumbtack.school.buscompany.validation.Id;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController extends BaseController{
    private final UserDao userDao;
    private final ClientDao clientDao;
    private final DateService dateService;

    public OrderController(UserDao userDao, ClientDao clientDao, DateService dateService){
        this.userDao = userDao;
        this.clientDao = clientDao;
        this.dateService = dateService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderTicketDtoResponse orderTicket(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @RequestBody @Valid OrderTicketDtoRequest request){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "orderTicket");
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "orderTicket");
        }

        Ticket ticket = OrderMapstructMapper.INSTANCE.fromDtoRequest(request);
        List<Passenger> passengers = new ArrayList<>();
        for(PassengerDtoRequest passengerDtoRequest : request.getPassengers()){
            Passenger passenger = OrderMapstructMapper.INSTANCE.fromDtoRequest(passengerDtoRequest);
            passengers.add(passenger);
        }
        ticket.setPassengers(passengers);

        clientDao.insertTicket(user.getId(), ticket);

        Trip trip = userDao.getTripById(ticket.getTripId());

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS, "orderTicket");
        }

        Order order = new Order();

        order.setId(ticket.getId());
        order.setTripId(trip.getId());
        order.setDate(ticket.getDate());
        order.setFromStation(trip.getFromStation());
        order.setToStation(trip.getToStation());
        order.setBusName(trip.getBusName());
        order.setStart(trip.getStart());
        order.setDuration(trip.getDuration());
        order.setPrice(trip.getPrice());
        order.setPassengers(ticket.getPassengers());
        order.calculateTotalPrice();

        int orderId = order.getId();

        List<Integer> occupiedSeats = clientDao.getOccupiedSeats(orderId);
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

        for(Passenger passenger : ticket.getPassengers()){
            PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
            passengerDtoResponses.add(passengerDtoResponse);
        }
        response.setPassengers(passengerDtoResponses);

        return response;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetOrdersDtoResponse getAllOrders(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                             @RequestParam(required = false) String fromStation,
                                             @RequestParam(required = false) String toStation,
                                             @RequestParam(required = false) String busName,
                                             @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String fromDate,
                                             @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String toDate,
                                             @RequestParam(required = false) @NumberFormat @Valid String clientId) throws ParseException {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getAllOrders");
        }

        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        Set<Order> orders = userDao.getAllOrders();

        if(fromStation != null){
            orders.retainAll(userDao.getOrdersFromStation(fromStation));
        }

        if(toStation != null){
            orders.retainAll(userDao.getOrdersToStation(toStation));
        }

        if(busName != null){
            orders.retainAll(userDao.getOrdersByBus(busName));
        }

        if(fromDate != null){
            orders.retainAll(userDao.getOrdersFromDate(dateService.parseDate(fromDate)));
        }

        if(toDate != null){
            orders.retainAll(userDao.getOrdersToDate(dateService.parseDate(toDate)));
        }

        if(userType.equals("admin")){

            if(clientId != null){
                orders.retainAll(userDao.getOrdersByClientId(Integer.parseInt(clientId)));
            }
        }
        else{
            orders.retainAll(userDao.getOrdersByClientId(user.getId()));
        }

        List<GetOrderDtoResponse> responseList = new ArrayList<>();
        for(Order order : orders){
            if(clientDao.isClientOrder(user, order)) {
                order.calculateTotalPrice();
                GetOrderDtoResponse getOrderDtoResponse = OrderMapstructMapper.INSTANCE.toGetDtoResponse(order);

                List<PassengerDtoResponse> passengers = new ArrayList<>();
                for (Passenger passenger : order.getPassengers()) {
                    PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
                    passengers.add(passengerDtoResponse);
                }
                getOrderDtoResponse.setPassengers(passengers);

                responseList.add(getOrderDtoResponse);
            }
        }

        return new GetOrdersDtoResponse(responseList);
    }

    @DeleteMapping(value = "/{orderId}")
    public CancelOrderDtoResponse cancelOrder(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @PathVariable @Id @Valid String orderId){
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
}
