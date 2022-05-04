package net.thumbtack.school.buscompany.place;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.PlaceController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.ChooseSeatDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PlaceController.class)
public class PlaceControllerOperationsTest extends BaseTest {

    @Test
    public void testGetFreePlaces() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie), RegisterTripDtoResponse.class).getTripId();
        httpPost("/api/trips/" + tripId + "/approve", cookie);
        logout(cookie);

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int orderId = getContent(registerOrder(client, tripId, "2022-10-04"), OrderTicketDtoResponse.class)
                .getOrderId();

        MvcResult result = httpGet("/api/places/" + orderId, client);

        GetFreePlacesDtoResponse response = getContent(result, GetFreePlacesDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(49, response.getPlaces().size());
        assertFalse(response.getPlaces().contains(0));
    }

    @Test
    public void testChoosePlace() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie), RegisterTripDtoResponse.class).getTripId();
        httpPost("/api/trips/" + tripId + "/approve", cookie);
        logout(cookie);

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int orderId = getContent(registerOrder(client, tripId, "2022-10-04"), OrderTicketDtoResponse.class)
                .getOrderId();

        ChooseSeatDtoRequest request = new ChooseSeatDtoRequest(
                orderId, "Смирнов", "Иван", "174830", 13
        );

        MvcResult result = httpPost("/api/places", client, gson.toJson(request));
        assertEquals(200, result.getResponse().getStatus());

        ChooseSeatDtoResponse response = getContent(result, ChooseSeatDtoResponse.class);
        assertEquals(orderId, response.getOrderId());
        assertEquals("Иван", response.getFirstName());
        assertEquals("Смирнов", response.getLastName());
        assertEquals("174830", response.getPassport());
        assertEquals("Билет " + orderId + "_13", response.getTicket());
        assertEquals(13, response.getPlace());
    }
}
