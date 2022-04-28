package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrdersDtoResponse;
import net.thumbtack.school.buscompany.service.ClientService;
import net.thumbtack.school.buscompany.service.UserService;
import net.thumbtack.school.buscompany.validation.Date;
import net.thumbtack.school.buscompany.validation.Id;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final ClientService clientService;

    public OrderController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public OrderTicketDtoResponse orderTicket(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @RequestBody @Valid OrderTicketDtoRequest request){
        return clientService.orderTicket(cookieValue, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetOrdersDtoResponse getAllOrders(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                             @RequestParam(required = false) String fromStation,
                                             @RequestParam(required = false) String toStation,
                                             @RequestParam(required = false) String busName,
                                             @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String fromDate,
                                             @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String toDate,
                                             @RequestParam(required = false) @NumberFormat @Valid String clientId) throws ParseException {
        return userService.getAllOrders(cookieValue, fromStation, toStation, busName, fromDate, toDate, clientId);
    }

    @DeleteMapping(value = "/{orderId}")
    public CancelOrderDtoResponse cancelOrder(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @PathVariable @Id @Valid String orderId){
        return clientService.cancelOrder(cookieValue, orderId);
    }
}
