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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
public class SessionController extends BaseController{
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionController.class);
    private final UserDao userDao;

    public SessionController(UserDao userDao){
        this.userDao = userDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginUserDtoResponse> loginUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                          @RequestBody @Valid LoginDtoRequest request){
        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION, "login");
        }

        User user = userDao.getByLogin(request.getLogin());
        String userType = userDao.getUserType(user);
        if(userDao.isOnline(user)){
            throw new BusCompanyException(ErrorCode.USER_ALREADY_ONLINE, "login");
        }

        if(!user.getPassword().equals(request.getPassword())){
            throw new BusCompanyException(ErrorCode.INVALID_LOGIN_OR_PASSWORD, "password");
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

        new Thread(() -> {
            try {
                for (int i = 0; i < cookieMaxAge; i++) {
                    Thread.sleep(1000);
                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
            }
            userDao.closeSession(uuid);
            LOGGER.info("User " + user.getLogin() + " disconnected");
        }).start();

        LOGGER.info("User " + user.getLogin() + " joined the server");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<LogoutUserDtoResponse> logoutUser(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "logout");
        }
        User user = userDao.getBySession(cookieValue);

        userDao.closeSession(cookieValue);

        LOGGER.info("User " + user.getLogin() + " left the server");

        ResponseCookie cookie = deleteJavaSessionCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new LogoutUserDtoResponse());
    }

}