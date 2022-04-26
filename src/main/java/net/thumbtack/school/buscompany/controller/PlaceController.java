package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.ChooseSeatDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.OrderMapstructMapper;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.User;
import net.thumbtack.school.buscompany.validation.Id;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {
    private final UserDao userDao;
    private final ClientDao clientDao;

    public PlaceController(UserDao userDao, ClientDao clientDao){
        this.userDao = userDao;
        this.clientDao = clientDao;
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetFreePlacesDtoResponse getFreePlaces(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                  @PathVariable @Id @Valid String orderId){
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getFreePlaces");
        }

        Order order = userDao.getOrderById(Integer.parseInt(orderId));

        if(order == null){
            throw new BusCompanyException(ErrorCode.ORDER_NOT_EXISTS, "order");
        }

        List<Integer> occupiedSeats = clientDao.getOccupiedSeats(Integer.parseInt(orderId));
        Bus bus = userDao.getBus(order.getBusName());
        int placeCount = Integer.parseInt(bus.getPlaceCount());

        List<Integer> freeSeats = new ArrayList<>();
        for(int placeNumber = 0; placeNumber < placeCount; placeNumber++){
            if(!occupiedSeats.contains(placeNumber)){
                freeSeats.add(placeNumber);
            }
        }

        return new GetFreePlacesDtoResponse(freeSeats);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChooseSeatDtoResponse chooseSeat(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                            @RequestBody @Valid ChooseSeatDtoRequest request){
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

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
