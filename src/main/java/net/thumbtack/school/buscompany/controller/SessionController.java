package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginUserDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.logout.LogoutUserDtoResponse;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController extends BaseController{
    private final UserDao userDao;

    public SessionController(UserDao userDao){
        this.userDao = userDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginUserDtoResponse> loginUser(@RequestBody @Valid LoginDtoRequest request){
        try{
            User user = userDao.getByLogin(request.getLogin());
            String userType = userDao.getUserType(user);
            if(userDao.isOnline(user)){
                throw new BusCompanyException(ErrorCode.USER_ALREADY_ONLINE);
            }

            if(!user.getPassword().equals(request.getPassword())){
                throw new BusCompanyException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
            }

            String uuid = userDao.openSession(user);
            ResponseCookie cookie = createJavaSessionIdCookie(uuid);

            LoginUserDtoResponse response;

            if(userType.equals("admin")){
                Admin admin = (Admin) user;
                response = new LoginAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                        admin.getPatronymic(), userType, admin.getPosition());
            }
            else{
                Client client = (Client) user;
                response = new LoginClientDtoResponse(client.getId(), client.getFirstName(), client.getLastName(),
                        client.getPatronymic(), userType, client.getEmail(), client.getPhone());
            }

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
        }
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

    @DeleteMapping
    public ResponseEntity<LogoutUserDtoResponse> logoutUser(@CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue) {
        try{
            userDao.closeSession(cookieValue);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, "").body(new LogoutUserDtoResponse());
        }
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

}