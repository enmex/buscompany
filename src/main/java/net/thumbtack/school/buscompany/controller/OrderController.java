package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrdersDtoResponse;
import net.thumbtack.school.buscompany.service.ClientService;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
    private final UserService userService;
    private final ClientService clientService;

    public OrderController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderTicketDtoResponse> orderTrip(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                            @RequestBody @Valid OrderTicketDtoRequest request){
        return clientService.orderTrip(cookieValue, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetOrdersDtoResponse> getAllOrders(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                             @RequestParam(required = false) String fromStation,
                                             @RequestParam(required = false) String toStation,
                                             @RequestParam(required = false) String busName,
                                             @RequestParam(required = false) String fromDate,
                                             @RequestParam(required = false) String toDate,
                                             @RequestParam(required = false) String clientId) {
        return userService.getAllOrders(cookieValue, fromStation, toStation, busName, fromDate, toDate, clientId);
    }

    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<CancelOrderDtoResponse> cancelOrder(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @PathVariable String orderId){
        return clientService.cancelOrder(cookieValue, orderId);
    }
}
