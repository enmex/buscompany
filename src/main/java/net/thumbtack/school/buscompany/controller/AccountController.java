package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.unregister.UnregisterUserDtoResponse;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return userService.unregisterUser(cookieValue);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetProfileDtoResponse getUserProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return userService.getUserProfile(cookieValue);
    }
}
