package net.thumbtack.school.buscompany.account;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.AccountController;
import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import net.thumbtack.school.buscompany.service.GlobalErrorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AccountController.class)
public class AccountControllerBadRequestsTest extends BaseTest {

    @Test
    public void testInvalidUnregister() throws Exception {
        MvcResult result = httpDelete("/api/accounts");
        assertBadRequest(result, "ONLINE_OPERATION");
    }

    @Test
    public void testInvalidGetUserProfile() throws Exception {
        MvcResult result = httpGet("/api/accounts");
        assertBadRequest(result, "ONLINE_OPERATION");
    }
}
