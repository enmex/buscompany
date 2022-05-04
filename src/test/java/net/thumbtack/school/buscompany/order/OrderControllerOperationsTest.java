package net.thumbtack.school.buscompany.order;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.OrderController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrdersDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
public class OrderControllerOperationsTest extends BaseTest {

    @Test
    public void testOrderTicket() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie), RegisterTripDtoResponse.class).getTripId();
        httpPost("/api/trips/" + tripId + "/approve", cookie);
        logout(cookie);

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = registerOrder(client, tripId, "2022-10-04");

        OrderTicketDtoResponse response = getContent(result, OrderTicketDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(500, response.getTotalPrice());
        assertEquals(1, response.getPassengers().size());
        assertTrue(response.getOrderId() > 0);
    }

    @Test
    public void testCancelOrder() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripId = getContent(registerDatesTrip(cookie), RegisterTripDtoResponse.class).getTripId();
        httpPost("/api/trips/" + tripId + "/approve", cookie);
        logout(cookie);

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        int orderId = getContent(registerOrder(client, tripId, "2022-10-04"),
                OrderTicketDtoResponse.class).getOrderId();

        MvcResult result = httpDelete("/api/orders/" + orderId, client);

        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    public void testGetAllOrders() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int tripOmskTumenId = getContent(registerScheduleTrip(cookie, "Автобус", "Омск", "Тюмень", "08:00",
                "24:00", "2022-05-06", "2022-06-06", "daily"),
                RegisterTripDtoResponse.class).getTripId();
        int tripMoscowPeterId = getContent(registerScheduleTrip(cookie, "Автобус", "Москва", "Петербург", "08:00",
                        "24:00", "2022-01-06", "2022-03-06", "even"),
                RegisterTripDtoResponse.class).getTripId();
        int standartDatesTrip = getContent(registerDatesTrip(cookie),
                RegisterTripDtoResponse.class).getTripId();

        httpPost("/api/trips/" + tripOmskTumenId + "/approve", cookie);
        httpPost("/api/trips/" + tripMoscowPeterId + "/approve", cookie);
        httpPost("/api/trips/" + standartDatesTrip + "/approve", cookie);

        MvcResult getAllOrdersAdmin = httpGet("/api/orders", cookie);
        assertEquals(200, getAllOrdersAdmin.getResponse().getStatus());

        GetOrdersDtoResponse ordersAdmin = getContent(getAllOrdersAdmin, GetOrdersDtoResponse.class);
        assertEquals(0, ordersAdmin.getOrders().size());

        logout(cookie);


        MvcResult resultClient1Register = registerClient();
        Cookie client1 = resultClient1Register.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        int idClient1 = getContent(resultClient1Register, RegisterClientDtoResponse.class).getId();

        MvcResult result = httpGet("/api/orders", client1);
        GetOrdersDtoResponse response = getContent(result, GetOrdersDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(0, response.getOrders().size());

        registerOrder(client1, standartDatesTrip, "2022-10-04");
        registerOrder(client1, tripOmskTumenId, "2022-05-08");
        registerOrder(client1, tripMoscowPeterId, "2022-01-08");

        logout(client1);
        Cookie client2 = registerClient("Иван", "Иванов",
                "Иванович", "ivan@mail.ru",
                "79998887766", "ivan_client", "123").getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        registerOrder(client2, standartDatesTrip, "2022-10-04");
        registerOrder(client2, tripOmskTumenId, "2022-05-08");
        registerOrder(client2, tripMoscowPeterId, "2022-01-08");
        logout(client2);

        Cookie admin = login("mich_admin", "123").getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult getAllByAdmin = httpGet("/api/orders", admin);

        GetOrdersDtoResponse getByAdmin = getContent(getAllByAdmin, GetOrdersDtoResponse.class);
        assertEquals(200, getAllByAdmin.getResponse().getStatus());
        assertEquals(6, getByAdmin.getOrders().size());

        MvcResult getAllByAdminClientId = httpGet("/api/orders?clientId=" + idClient1, admin);
        GetOrdersDtoResponse getByAdminClientId = getContent(getAllByAdminClientId, GetOrdersDtoResponse.class);
        assertEquals(3, getByAdminClientId.getOrders().size());
        logout(admin);

        Cookie client_mich = login("mich_client", "123").getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult resultForClient = httpGet("/api/orders", client_mich);
        assertEquals(200, resultForClient.getResponse().getStatus());

        GetOrdersDtoResponse responseClient = getContent(resultForClient, GetOrdersDtoResponse.class);

        assertEquals(3, responseClient.getOrders().size());
    }
}
