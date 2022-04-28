package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterAdminDtoResponse;
import net.thumbtack.school.buscompany.service.AdminService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                                  @RequestBody @Valid RegisterAdminDtoRequest request){
        return adminService.registerAdmin(cookieValue, request);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdateUserProfileDtoResponse updateAdminProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                                           @RequestBody @Valid UpdateAdminProfileDtoRequest request){
        return adminService.updateAdminProfile(cookieValue, request);
    }

}
