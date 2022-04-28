package net.thumbtack.school.buscompany.client;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.AdminController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateClientProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AdminController.class)
public class ClientOperationsTest extends BaseTest {

    @Test
    public void testRegisterClient() throws Exception {
        MvcResult result = registerClient();

        RegisterClientDtoResponse response = gson.fromJson(result
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), RegisterClientDtoResponse.class);
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("client", response.getUserType());
        assertEquals("Михаил", response.getFirstName());
        assertEquals("Привалов", response.getLastName());
        assertEquals("Андреевич", response.getPatronymic());
        assertEquals("mikhail@mail.ru", response.getEmail());
        assertEquals("79998887766", response.getPhone());
    }

    @Test
    public void testUpdateClientProfile() throws Exception {
        MvcResult registerResult = registerClient();
        UpdateClientProfileDtoRequest request = new UpdateClientProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "123", "456", "mail@mail.ru", "79998887766"
        );

        Cookie cookie = registerResult.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpPut("/api/clients", cookie, gson.toJson(request));
        UpdateClientProfileDtoResponse response = gson.fromJson(result
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), UpdateClientProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("client", response.getUserType());
        assertEquals("Иван", response.getFirstName());
        assertEquals("Иванов", response.getLastName());
        assertEquals("Иванович", response.getPatronymic());
        assertEquals("79998887766", response.getPhone());
        assertEquals("mail@mail.ru", response.getEmail());
    }
}
