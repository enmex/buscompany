package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateClientProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.GetAllClientsDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import net.thumbtack.school.buscompany.service.AdminService;
import net.thumbtack.school.buscompany.service.ClientService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final AdminService adminService;
    private final ClientService clientService;

    public ClientController(AdminService adminService, ClientService clientService) {
        this.adminService = adminService;
        this.clientService = clientService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterClientDtoResponse> registerClient(@RequestBody @Valid RegisterClientDtoRequest request){
        return clientService.registerClient(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetAllClientsDtoResponse getAllClients(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        return adminService.getAllClients(cookieValue);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UpdateUserProfileDtoResponse updateClientProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                                            @RequestBody @Valid UpdateClientProfileDtoRequest request){
        return clientService.updateClientProfile(cookieValue, request);
    }
}
