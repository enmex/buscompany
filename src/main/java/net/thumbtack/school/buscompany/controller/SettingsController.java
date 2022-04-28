package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.common.settings.GetSettingsDtoResponse;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetSettingsDtoResponse getSettings(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return userService.getSettings(cookieValue);
    }
}
