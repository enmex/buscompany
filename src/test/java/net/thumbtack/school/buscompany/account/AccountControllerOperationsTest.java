package net.thumbtack.school.buscompany.account;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.AccountController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
public class AccountControllerOperationsTest extends BaseTest {

    @Test
    public void testUnregister() throws Exception {
        Cookie adminCookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult resultUnregisterAdmin = httpDelete("/api/accounts", adminCookie);

        assertEquals(401, resultUnregisterAdmin.getResponse().getStatus());
        assertEquals(0, resultUnregisterAdmin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getMaxAge());
        assertEquals("", resultUnregisterAdmin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getValue());

        Cookie clientCookie = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult resultUnregisterClient = httpDelete("/api/accounts", clientCookie);

        assertEquals(401, resultUnregisterClient.getResponse().getStatus());
        assertEquals(0, resultUnregisterClient.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getMaxAge());
        assertEquals("", resultUnregisterClient.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getValue());
    }

    @Test
    public void testGetAdminProfile() throws Exception {
        Cookie adminCookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpGet("/api/accounts", adminCookie);

        GetAdminProfileDtoResponse response = getContent(result, GetAdminProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Михаил", response.getFirstName());
        assertEquals("Привалов", response.getLastName());
        assertEquals("Андреевич", response.getPatronymic());
        assertEquals("Директор", response.getPosition());
        assertEquals("admin", response.getUserType());
    }

    @Test
    public void testGetClientProfile() throws Exception {
        Cookie clientCookie = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpGet("/api/accounts", clientCookie);

        GetClientProfileDtoResponse response = getContent(result, GetClientProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Михаил", response.getFirstName());
        assertEquals("Привалов", response.getLastName());
        assertEquals("Андреевич", response.getPatronymic());
        assertEquals("mikhail@mail.ru", response.getEmail());
        assertEquals("79998887766", response.getPhone());
        assertEquals("client", response.getUserType());
    }
}
