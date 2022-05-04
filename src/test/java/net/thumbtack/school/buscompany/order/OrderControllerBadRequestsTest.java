package net.thumbtack.school.buscompany.order;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.OrderController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
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
@WebMvcTest(controllers = OrderController.class)
public class OrderControllerBadRequestsTest extends BaseTest {
    @Test
    public void testBadOrderTrip() throws Exception {
        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int tripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();

        assertBadRequest(registerOrder(admin, tripId, "2022-09-09"), "OPERATION_NOT_ALLOWED");
        assertBadRequest(registerOrder(client, 100, "2022-09-09"), "TRIP_NOT_EXISTS");
        assertInvalidRequest(registerOrder(client, tripId, "201030243"), 1);
        assertBadRequest(registerOrder(client, tripId, "2022-01-07"), "TRIP_NOT_APPROVED");

        httpPost("/api/trips/" + tripId + "/approve", admin);

        assertBadRequest(registerOrder(client, tripId, "2022-10-10"), "NO_TRIP_ON_THIS_DATE");

        Cookie anotherClient = registerClient("Иван", "Иванов", "Иванович",
                "ivan@mail.ru", "78889996644", "ivan_client", "123").getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        List<PassengerDtoRequest> passengers = new ArrayList<>();
        for(int i = 0; i < 50; i++){
            PassengerDtoRequest passengerDtoRequest = new PassengerDtoRequest();
            passengerDtoRequest.setFirstName("Михаил");
            passengerDtoRequest.setLastName("Привалов");
            passengerDtoRequest.setPassport("123");
            passengers.add(passengerDtoRequest);
        }

        registerOrder(anotherClient, tripId, "2022-09-02", passengers);
        PassengerDtoRequest passengerDtoRequest = new PassengerDtoRequest();
        passengerDtoRequest.setPassport("456");
        passengerDtoRequest.setFirstName("Иван");
        passengerDtoRequest.setLastName("Иванов");
        List<PassengerDtoRequest> passengerDtoRequests = new ArrayList<>();
        passengerDtoRequests.add(passengerDtoRequest);

        assertBadRequest(registerOrder(client, tripId, "2022-09-02", passengerDtoRequests), "NO_FREE_PLACES");
    }

    @Test
    public void testBadCancelOrder() throws Exception {
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

        assertBadRequest(httpDelete("/api/orders/" + orderId, admin), "OPERATION_NOT_ALLOWED");
        assertBadRequest(httpDelete("/api/orders/100", client), "ORDER_NOT_EXISTS");
    }

    @Test
    public void testBadGetAllOrders() throws Exception {
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

        registerOrder(client, tripId, "2022-09-02", passengers);
        registerOrder(client, tripId, "2022-09-03", passengers);
        registerOrder(client, tripId, "2022-09-04", passengers);

        assertBadRequest(httpGet("/api/orders?fromDate=dsdsd", admin), "INVALID_DATE");
        assertBadRequest(httpGet("/api/orders?toDate=dsdsd", admin), "INVALID_DATE");
        assertBadRequest(httpGet("/api/orders?clientId=abc", admin), "INVALID_ID");
    }

}
