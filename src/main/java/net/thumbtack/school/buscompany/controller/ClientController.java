package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateClientProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.GetAllClientsDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.ClientMapstructMapper;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/api/clients")
public class ClientController extends BaseController{
    private final UserDao userDao;
    private final AdminDao adminDao;
    private final String userType = "client";

    public ClientController(UserDao userDao, AdminDao adminDao){
        this.userDao = userDao;
        this.adminDao = adminDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterClientDtoResponse> registerClient(@RequestBody @Valid RegisterClientDtoRequest request,
                                                                    @CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){

        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION);
        }

        Client client = ClientMapstructMapper.INSTANCE.fromRegisterDto(request);

        String uuid = userDao.register(client);

        ResponseCookie cookie = createJavaSessionIdCookie(uuid);

        new Thread(() -> {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    userDao.closeSession(uuid);
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, cookieMaxAge * 1000);
        }).start();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                new RegisterClientDtoResponse(client.getId(), client.getFirstName(), client.getLastName(),
                        client.getPatronymic(), userType, client.getEmail(), client.getPhone())
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllClientsDtoResponse> getAllClients(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getAllFields");
        }

        List<Client> clients = adminDao.getAllClients();
        List<GetClientProfileDtoResponse> clientProfiles = new ArrayList<>();

        for(Client client : clients){
            GetClientProfileDtoResponse response = ClientMapstructMapper.INSTANCE.toGetProfileDto(client);
            response.setUserType("client");
            clientProfiles.add(response);
        }

        return ResponseEntity.ok().body(new GetAllClientsDtoResponse(clientProfiles));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateUserProfileDtoResponse> updateClientProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                                            @RequestBody @Valid UpdateClientProfileDtoRequest request){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("client")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "updateClientProfile");
        }

        Client client = (Client) user;

        if(!client.getPassword().equals(request.getOldPassword())){
            throw new BusCompanyException(ErrorCode.INVALID_PASSWORD, "password");
        }

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPassword(request.getNewPassword());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        userDao.updateUser(client);

        return ResponseEntity.ok().body(new UpdateClientProfileDtoResponse(
                client.getFirstName(), client.getLastName(), client.getPatronymic(),
                userType, client.getEmail(), client.getPhone()
        ));
    }
}
