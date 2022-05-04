package net.thumbtack.school.buscompany.session;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.SessionController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SessionController.class)
public class SessionControllerBadRequestsTest extends BaseTest {

    @Test
    public void testBadLogin() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        logout(admin);

        LoginDtoRequest requestLogin = new LoginDtoRequest(
                "mich_admin", "dsda"
        );
        LoginDtoRequest invalidRequestLogin = new LoginDtoRequest();

        assertInvalidRequest(httpPost("/api/sessions", gson.toJson(invalidRequestLogin)), 2);
        assertBadRequest(httpPost("/api/sessions", gson.toJson(requestLogin)), "INVALID_LOGIN_OR_PASSWORD");
    }

    @Test
    public void testBadLogout() throws Exception {
        assertBadRequest(httpDelete("/api/sessions"), "ONLINE_OPERATION");
    }

}
