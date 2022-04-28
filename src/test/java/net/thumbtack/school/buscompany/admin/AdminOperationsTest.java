package net.thumbtack.school.buscompany.admin;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.AdminController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterAdminDtoResponse;
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
public class AdminOperationsTest extends BaseTest {

    @Test
    public void testRegisterAdmin() throws Exception {
        MvcResult result = registerAdmin();
        RegisterAdminDtoResponse response = gson.fromJson(result
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), RegisterAdminDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("admin", response.getUserType());
        assertEquals("Михаил", response.getFirstName());
        assertEquals("Привалов", response.getLastName());
        assertEquals("Андреевич", response.getPatronymic());
        assertEquals("Директор", response.getPosition());
    }

    @Test
    public void testUpdateAdminProfile() throws Exception {
        MvcResult registerResult = registerAdmin();
        UpdateAdminProfileDtoRequest request = new UpdateAdminProfileDtoRequest(
                  "Иван", "Иванов", "Иванович", "123", "456", "Директор"
        );

        Cookie cookie = registerResult.getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpPut("/api/admins", cookie, gson.toJson(request));
        UpdateAdminProfileDtoResponse response = gson.fromJson(result
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), UpdateAdminProfileDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals("admin", response.getUserType());
        assertEquals("Иван", response.getFirstName());
        assertEquals("Иванов", response.getLastName());
        assertEquals("Иванович", response.getPatronymic());
        assertEquals("Директор", response.getPosition());
    }

}
