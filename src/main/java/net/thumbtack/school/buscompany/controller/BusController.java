package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.admin.RegisterBusDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.GetAllBusesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterBusDtoResponse;
import net.thumbtack.school.buscompany.service.AdminService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    private final AdminService adminService;

    public BusController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterBusDtoResponse> registerBus(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                              @RequestBody @Valid RegisterBusDtoRequest request){
        return adminService.registerBus(cookieValue, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllBusesDtoResponse> getAllBuses(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return adminService.getAllBuses(cookieValue);
    }
}
