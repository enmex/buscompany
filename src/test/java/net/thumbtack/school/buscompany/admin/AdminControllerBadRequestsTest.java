package net.thumbtack.school.buscompany.admin;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.AdminController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AdminController.class)
public class AdminControllerBadRequestsTest extends BaseTest {
    @Test
    public void testBadAdminRegister() throws Exception {
        MvcResult result = registerAdmin("", "", "", "", "", "");
        assertInvalidRequest(result, 7);

        result = registerAdmin("123", "*$^#", "3$3", "3CX7", "-=-=23", "123");

        assertInvalidRequest(result, 6);
    }

    @Test
    public void testBadUpdateAdminProfile() throws Exception {
        MvcResult result = httpPut("/api/admins", gson.toJson(new UpdateAdminProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "123", "456", "Директор"
        )));

        assertBadRequest(result, "ONLINE_OPERATION");

        Cookie client = registerClient().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        result = httpPut("/api/admins", client, gson.toJson(new UpdateAdminProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "123", "456", "Директор"
        )));
        assertBadRequest(result, "OPERATION_NOT_ALLOWED");

        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);
        result = httpPut("/api/admins", admin,
                gson.toJson(new UpdateAdminProfileDtoRequest("", "", "", "", "", "")));
        assertInvalidRequest(result, 7);

        result = httpPut("/api/admins",admin, gson.toJson(new UpdateAdminProfileDtoRequest("123", "213", "124",
                "432", "432", "3423")));

        assertInvalidRequest(result, 6);

        result = httpPut("/api/admins", admin, gson.toJson(new UpdateAdminProfileDtoRequest(
                "Иван", "Иванов", "Иванович", "678", "456", "Директор")));
        assertBadRequest(result, "INVALID_PASSWORD");
    }
}
