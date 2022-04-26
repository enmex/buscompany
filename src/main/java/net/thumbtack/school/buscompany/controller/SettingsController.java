package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.response.common.settings.GetSettingsDtoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    @Value("${buscompany.user_idle_timeout}")
    private int userIdleTimeout;
    @Value("${buscompany.min_password_length}")
    private int minPasswordLength;
    @Value("${buscompany.max_name_length}")
    private int maxNameLength;

    private final UserDao userDao;

    public SettingsController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetSettingsDtoResponse getSettings(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return new GetSettingsDtoResponse(
                maxNameLength, userIdleTimeout, minPasswordLength
        );
    }
}
