package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.unregister.UnregisterUserDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController extends BaseController{
    private final UserDao userDao;

    public AccountController(UserDao userDao){
        this.userDao = userDao;
    }

    @DeleteMapping
    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "unregister");
        }

        User user = userDao.getBySession(cookieValue);
        userDao.unregister(user);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, "").body(
                new UnregisterUserDtoResponse()
        );
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDtoResponse> getUserProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getProfile");
        }

        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        GetProfileDtoResponse response;

        if(userType.equals("admin")){
            Admin admin = (Admin) user;
            response = new GetAdminProfileDtoResponse(
                    admin.getId(), admin.getFirstName(), admin.getLastName(),
                    admin.getPatronymic(), userType, admin.getPosition()
            );
        }
        else{
            Client client = (Client) user;
            response = new GetClientProfileDtoResponse(
                    client.getId(), client.getFirstName(), client.getLastName(),
                    client.getPatronymic(), userType, client.getEmail(), client.getPhone()
            );
        }

        return ResponseEntity.ok().body(response);
    }
}
