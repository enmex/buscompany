package net.thumbtack.school.buscompany.bus;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.BusController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.admin.RegisterBusDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.GetAllBusesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterBusDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BusController.class)
public class BusControllerOperationsTest extends BaseTest {

    @Test
    public void testRegisterBus() throws Exception {
        Cookie adminCookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        RegisterBusDtoRequest request = new RegisterBusDtoRequest("Автобус", 40);

        MvcResult result = httpPost("/api/buses", adminCookie, gson.toJson(request));
        RegisterBusDtoResponse response = getContent(result, RegisterBusDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Автобус", response.getBusName());
        assertEquals(40, response.getPlaceCount());
    }

    @Test
    public void testGetAllBuses() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        httpPost("/api/buses", cookie, gson.toJson(
                new RegisterBusDtoRequest("Автобус1", 40)
        ));
        httpPost("/api/buses", cookie, gson.toJson(
                new RegisterBusDtoRequest("Автобус2", 45)
        ));
        httpPost("/api/buses", cookie, gson.toJson(
                new RegisterBusDtoRequest("Автобуc3", 50)
        ));

        MvcResult result = httpGet("/api/buses", cookie);

        GetAllBusesDtoResponse response = getContent(result, GetAllBusesDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(3, response.getBuses().size());

        for(BusDtoResponse busDtoResponse : response.getBuses()){
            assertNotNull(busDtoResponse.getBusName());
            assertTrue(busDtoResponse.getPlaceCount() > 0);
        }
    }
}
