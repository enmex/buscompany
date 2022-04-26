package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterAdminDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.AdminMapstructMapper;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/api/admins")
public class AdminController extends BaseController{
    private final UserDao userDao;
    private final String userType = "admin";

    public AdminController(UserDao userDao){
        this.userDao = userDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(@RequestBody @Valid RegisterAdminDtoRequest request,
                                                                  @CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION, "register");
        }

        Admin admin = AdminMapstructMapper.INSTANCE.fromRegisterDto(request);
        String uuid = userDao.register(admin);

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
                new RegisterAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                        admin.getPatronymic(), userType, admin.getPosition())
        );
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateUserProfileDtoResponse> updateAdminProfile(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                                           @RequestBody @Valid UpdateAdminProfileDtoRequest request){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "updateAdminProfile");
        }

        Admin admin = (Admin) user;

        String oldPassword = request.getOldPassword();

        if(!oldPassword.equals(user.getPassword())){
            throw new BusCompanyException(ErrorCode.INVALID_PASSWORD, "password");
        }

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPatronymic(request.getPatronymic());
        admin.setPassword(request.getNewPassword());
        admin.setPosition(request.getPosition());

        userDao.updateUser(user);

        return ResponseEntity.ok().body(new UpdateAdminProfileDtoResponse(
                admin.getFirstName(), admin.getLastName(), admin.getPatronymic(),
                userType, admin.getPosition()
        ));
    }

}
