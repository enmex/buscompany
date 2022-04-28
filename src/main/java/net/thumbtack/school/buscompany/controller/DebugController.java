package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.dto.response.ClearDatabaseDtoResponse;
import net.thumbtack.school.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/debug")
public class DebugController {
    private final UserService userService;

    public DebugController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    public ClearDatabaseDtoResponse clear(){
        return userService.clearAll();
    }
}
