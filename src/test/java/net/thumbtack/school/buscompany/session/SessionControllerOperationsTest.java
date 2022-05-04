package net.thumbtack.school.buscompany.session;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.SessionController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SessionController.class)
public class SessionControllerOperationsTest extends BaseTest {

    @Test
    public void testLogout() throws Exception {
        MvcResult admin = registerAdmin();

        Cookie cookie = admin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult resultLogoutAdmin = logout(cookie);
        assertEquals(401, resultLogoutAdmin.getResponse().getStatus());
        assertEquals(0, resultLogoutAdmin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getMaxAge());
        assertEquals("", resultLogoutAdmin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getValue());

        MvcResult client = registerClient();

        cookie = client.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        MvcResult resultLogoutClient = logout(cookie);
        assertEquals(401, resultLogoutClient.getResponse().getStatus());
        assertEquals(0, resultLogoutClient.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getMaxAge());
        assertEquals("", resultLogoutClient.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getValue());
    }

    @Test
    public void testLogin() throws Exception {
        MvcResult admin = registerAdmin();

        Cookie cookie = admin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        logout(cookie);

        MvcResult resultLogin = login("mich_admin", "123");
        assertEquals(200, resultLogin.getResponse().getStatus());
        assertEquals(3600, resultLogin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getMaxAge());
        assertNotNull(resultLogin.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID).getValue());
    }
}
