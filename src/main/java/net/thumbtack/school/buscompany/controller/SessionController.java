package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginUserDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.logout.LogoutUserDtoResponse;
import net.thumbtack.school.buscompany.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);
    private final UserService userService;

    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginUserDtoResponse> loginUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                          @RequestBody @Valid LoginDtoRequest request){
        return userService.loginUser(cookieValue, request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<LogoutUserDtoResponse> logoutUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue) {
        return userService.logoutUser(cookieValue);
    }

}