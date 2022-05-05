package net.thumbtack.school.buscompany;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.error.ErrorDtoResponse;
import net.thumbtack.school.buscompany.service.GlobalErrorHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
@ExtendWith(SpringExtension.class)
public class TestBadClientRequests extends BaseTest {
    @Test
    public void testMethodNotAllowedRequest() throws Exception {
        Cookie admin = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpGet("/api/admins", admin);

        assertEquals(405, result.getResponse().getStatus());

        ErrorDtoResponse response = getContent(result, ErrorDtoResponse.class);
        assertEquals(1, response.getErrors().size());

        GlobalErrorHandler.Error error = response.getErrors().get(0);

        assertEquals("METHOD_NOT_ALLOWED", error.getErrorCode());
    }

    @Test
    public void testNotFoundRequest() throws Exception {
        MvcResult result = httpGet("/");

        assertEquals(404, result.getResponse().getStatus());

        GlobalErrorHandler.Error error = getContent(result, ErrorDtoResponse.class).getErrors().get(0);

        assertEquals("NOT_FOUND", error.getErrorCode());
    }

    @Test
    public void testBadJsonRequest() throws Exception {
        MvcResult result = httpPost("/api/admins", "{\n" +
                "    \"firstName\":\"Михаил\",\n" +
                "    \"lastName\":\"Привалов\",\n" +
                "    \"position\":\"Директор\",\n" +
                "    \"login\":\"mich\",\n" +
                "    \"password\":\"123\"\n");

        assertEquals(400, result.getResponse().getStatus());

        GlobalErrorHandler.Error error = getContent(result, ErrorDtoResponse.class).getErrors().get(0);
        assertEquals("WRONG_JSON_FORMAT", error.getErrorCode());
    }
}
