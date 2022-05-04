package net.thumbtack.school.buscompany.place;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.PlaceController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PlaceController.class)
public class PlaceControllerBadRequestsTest extends BaseTest {

    @Test
    public void testBadGetFreePlaces() throws Exception {
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        List<PassengerDtoRequest> passengers = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            PassengerDtoRequest passengerDtoRequest = new PassengerDtoRequest();
            passengerDtoRequest.setFirstName("Михаил");
            passengerDtoRequest.setLastName("Привалов");
            passengerDtoRequest.setPassport("123");
            passengers.add(passengerDtoRequest);
        }

        int orderId = getContent(registerOrder(client, tripId, "2022-09-02", passengers), OrderTicketDtoResponse.class).getOrderId();

        assertBadRequest(httpGet("/api/places/" + orderId, admin), "OPERATION_NOT_ALLOWED");
        assertBadRequest(httpGet("/api/places/100", client), "ORDER_NOT_EXISTS");
        assertBadRequest(httpGet("/api/places/abc", client), "INVALID_ID");
    }

    @Test
    public void testBadChoosePlace() throws Exception {
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();
        httpPost("/api/trips/" + tripId + "/approve", admin);

        List<PassengerDtoRequest> passengers = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            PassengerDtoRequest passengerDtoRequest = new PassengerDtoRequest();
            passengerDtoRequest.setFirstName("Михаил");
            passengerDtoRequest.setLastName("Привалов");
            passengerDtoRequest.setPassport("123");
            passengers.add(passengerDtoRequest);
        }

        int orderId = getContent(registerOrder(client, tripId, "2022-09-02", passengers), OrderTicketDtoResponse.class).getOrderId();

        ChooseSeatDtoRequest request1 = new ChooseSeatDtoRequest();
        request1.setFirstName("Иван");
        request1.setLastName("Иванов");
        request1.setPassport("123");
        request1.setOrderId(orderId);
        request1.setPlace(46);

        ChooseSeatDtoRequest request2 = new ChooseSeatDtoRequest();
        request2.setFirstName("Михаил");
        request2.setLastName("Смирнов");
        request2.setPassport("456");
        request2.setOrderId(orderId);
        request2.setPlace(46);

        assertBadRequest(httpPost("/api/places", admin, gson.toJson(request1)), "OPERATION_NOT_ALLOWED");

        httpPost("/api/places", client, gson.toJson(request1));

        assertBadRequest(httpPost("/api/places", client, gson.toJson(request2)), "PLACE_IS_OCCUPIED");
    }
}
