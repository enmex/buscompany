package net.thumbtack.school.buscompany.integration;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class IntegrationTest extends BaseTest {
    @Test
    public void integrationTest() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client1 = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client2 = registerClient("Иван", "Иванов",
                "Иванович", "ivan@mail.ru",
                "79998887766", "ivan", "123").getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        Cookie client3 = registerClient("Михаил", "Смирнов", "Петрович",
                "smirnov@mail.ru", "89837654312", "smirn", "123")
                .getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        assertNotNull(client1.getValue());
        assertNotNull(client2.getValue());
        assertNotNull(client3.getValue());
        assertNotNull(admin.getValue());

        int scheduleTripId = getContent(registerScheduleTrip(admin), RegisterTripDtoResponse.class).getTripId();
        int datesTripId = getContent(registerDatesTrip(admin), RegisterTripDtoResponse.class).getTripId();

        assertBadRequest(registerOrder(client1, scheduleTripId, "2022-04-10"), "TRIP_NOT_APPROVED");

        httpPost("/api/trips/" + scheduleTripId + "/approve", admin);
        httpPost("/api/trips/" + datesTripId + "/approve", admin);

        assertBadRequest(registerOrder(client1, scheduleTripId, "2022-04-10"), "NO_TRIP_ON_THIS_DATE");

        MvcResult resultRegisterScheduleOrderClient1 = registerOrder(client1, scheduleTripId, "2022-09-03");
        MvcResult resultRegisterScheduleOrderClient2 = registerOrder(client2, scheduleTripId, "2022-09-03");
        MvcResult resultRegisterScheduleOrderClient3 = registerOrder(client3, scheduleTripId, "2022-09-03");
        MvcResult resultRegisterDatesOrderClient1 = registerOrder(client1, datesTripId, "2022-10-04");
        MvcResult resultRegisterDatesOrderClient2 = registerOrder(client2, datesTripId, "2022-10-04");
        MvcResult resultRegisterDatesOrderClient3 = registerOrder(client3, datesTripId, "2022-10-04");

        assertEquals(200, resultRegisterScheduleOrderClient1.getResponse().getStatus());
        assertEquals(200, resultRegisterScheduleOrderClient2.getResponse().getStatus());
        assertEquals(200, resultRegisterScheduleOrderClient3.getResponse().getStatus());
        assertEquals(200, resultRegisterDatesOrderClient1.getResponse().getStatus());
        assertEquals(200, resultRegisterDatesOrderClient2.getResponse().getStatus());
        assertEquals(200, resultRegisterDatesOrderClient3.getResponse().getStatus());

        int idScheduleOrderClient1 = getContent(resultRegisterScheduleOrderClient1, OrderTicketDtoResponse.class).getOrderId();
        int idScheduleOrderClient2 = getContent(resultRegisterScheduleOrderClient2, OrderTicketDtoResponse.class).getOrderId();
        int idScheduleOrderClient3 = getContent(resultRegisterScheduleOrderClient3, OrderTicketDtoResponse.class).getOrderId();
        int idDatesOrderClient1 = getContent(resultRegisterDatesOrderClient1, OrderTicketDtoResponse.class).getOrderId();
        int idDatesOrderClient2 = getContent(resultRegisterDatesOrderClient2, OrderTicketDtoResponse.class).getOrderId();
        int idDatesOrderClient3 = getContent(resultRegisterDatesOrderClient3, OrderTicketDtoResponse.class).getOrderId();

        GetClientProfileDtoResponse getProfileResponse = getContent(httpGet("/api/accounts", client3), GetClientProfileDtoResponse.class);
        assertEquals("Михаил", getProfileResponse.getFirstName());
        assertEquals("Смирнов", getProfileResponse.getLastName());
        assertEquals("Петрович", getProfileResponse.getPatronymic());
        assertEquals("smirnov@mail.ru", getProfileResponse.getEmail());
        assertEquals("89837654312", getProfileResponse.getPhone());
        assertEquals("client", getProfileResponse.getUserType());

        assertEquals(401, logout(admin).getResponse().getStatus());

        assertBadRequest(login("mich_admin", "5443"), "INVALID_LOGIN_OR_PASSWORD");
        MvcResult loginAdminResult = login("mich_admin", "123");
        Cookie admin1 = loginAdminResult.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        assertEquals(200, loginAdminResult.getResponse().getStatus());

        UpdateAdminProfileDtoRequest updateAdminProfileDtoRequest = new UpdateAdminProfileDtoRequest();
        updateAdminProfileDtoRequest.setPosition("Главный управляющий");
        updateAdminProfileDtoRequest.setFirstName("Михаил");
        updateAdminProfileDtoRequest.setLastName("Привалов");
        updateAdminProfileDtoRequest.setPatronymic("Андреевич");
        updateAdminProfileDtoRequest.setOldPassword("123");
        updateAdminProfileDtoRequest.setNewPassword("456");

        assertEquals(200, httpPut("/api/admins", admin1, gson.toJson(updateAdminProfileDtoRequest)).getResponse().getStatus());
        assertEquals(401, logout(admin1).getResponse().getStatus());

        MvcResult loginAdminResult1 = login("mich_admin", "456");
        assertEquals(200, loginAdminResult1.getResponse().getStatus());

        Cookie admin2 = loginAdminResult1.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        ChoosePlaceDtoRequest request = new ChoosePlaceDtoRequest();
        request.setPlace(30);
        request.setPassport("123");
        request.setFirstName("Иван");
        request.setLastName("Иванов");
        request.setOrderId(idScheduleOrderClient1);
        assertEquals(200, httpPost("/api/places", client1, gson.toJson(request)).getResponse().getStatus());

        GetFreePlacesDtoResponse getFreePlacesDtoResponse = getContent(httpGet("/api/places/" + idScheduleOrderClient1, client1), GetFreePlacesDtoResponse.class);
        assertTrue(getFreePlacesDtoResponse.getPlaces().contains(0));
        assertTrue(getFreePlacesDtoResponse.getPlaces().contains(1));
        assertTrue(getFreePlacesDtoResponse.getPlaces().contains(2));
        assertFalse(getFreePlacesDtoResponse.getPlaces().contains(30));

        assertBadRequest(httpDelete("/api/orders/" + idDatesOrderClient3, client1), "NOT_CLIENT_ORDER");
        assertEquals(200, httpDelete("/api/orders/" + idDatesOrderClient3, client3).getResponse().getStatus());

        List<String> dates = new ArrayList<>();
        dates.add("2022-06-01");
        int tripId = getContent(registerDatesTrip(admin2, "Лиаз", "Омск", "Новосибирск", "10:00", "18:00", dates),
                RegisterTripDtoResponse.class).getTripId();
        assertEquals(200, httpDelete("/api/trips/" + tripId, admin2).getResponse().getStatus());
    }
}
