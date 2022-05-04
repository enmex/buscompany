package net.thumbtack.school.buscompany.client;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.ClientController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateClientProfileDtoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ClientController.class)
public class ClientControllerBadRequestsTest extends BaseTest {
    @Test
    public void testBadClientRegister() throws Exception {
        MvcResult result = registerClient("", "", "", "", "", "", "");
        assertInvalidRequest(result, 7);

        result = registerClient("123", "*$^#", "3$3", "3CX7", "-=-=23", "123", "534dsf");

        assertInvalidRequest(result, 9);
    }

    @Test
    public void testBadUpdateClientProile() throws Exception {
        MvcResult result = httpPut("/api/clients", gson.toJson(new UpdateClientProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "123", "456", "michael@mail.ru", "79998887766"
        )));

        assertBadRequest(result, "ONLINE_OPERATION");

        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        result = httpPut("/api/clients", admin, gson.toJson(new UpdateClientProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "123", "456", "mail@mail.ru", "79998887766"
        )));
        assertBadRequest(result, "OPERATION_NOT_ALLOWED");

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        result = httpPut("/api/clients", client,
                gson.toJson(new UpdateClientProfileDtoRequest("", "", "", "", "", "", "")));
        assertInvalidRequest(result, 8);

        result = httpPut("/api/clients",admin, gson.toJson(new UpdateClientProfileDtoRequest("123", "213", "124",
                "432", "432", "3423", "%^(^$@8")));

        assertInvalidRequest(result, 9);

        result = httpPut("/api/clients", client, gson.toJson(new UpdateClientProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "678", "456", "mail@mail.ru", "79998887766")));
        assertBadRequest(result, "INVALID_PASSWORD");
    }
}
