package net.thumbtack.school.buscompany.settings;

import net.thumbtack.school.buscompany.BaseTest;
import net.thumbtack.school.buscompany.controller.SettingsController;
import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.common.settings.GetSettingsDtoResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SettingsController.class)
public class SettingsControllerOperationsTest extends BaseTest {

    @Test
    public void testGetSettings() throws Exception {
        Cookie cookie = registerAdmin().getResponse().getCookie(BusCompanyCookies.JAVASESSIONID);

        MvcResult result = httpGet("/api/settings", cookie);
        GetSettingsDtoResponse response = getContent(result, GetSettingsDtoResponse.class);

        assertEquals(200, result.getResponse().getStatus());
        assertEquals(3600, response.getUserIdleTimeout());
        assertEquals(50, response.getMaxNameLength());
        assertEquals(2, response.getMinPasswordLength());
    }
}
