package net.thumbtack.school.buscompany.controller;

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
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminController extends BaseController{
    private final UserDao userDao;
    private final String userType = "admin";

    public AdminController(UserDao userDao){
        this.userDao = userDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(@RequestBody @Valid RegisterAdminDtoRequest request){
        try {
            Admin admin = AdminMapstructMapper.INSTANCE.fromRegisterDto(request);
            String uuid = userDao.register(admin);

            ResponseCookie cookie = createJavaSessionIdCookie(uuid);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                    new RegisterAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                            admin.getPatronymic(), userType, admin.getPosition())
            );
        }
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateUserProfileDtoResponse> updateAdminProfile(@CookieValue(name = "JAVASESSIONID") String cookieValue,
                                                                           @RequestBody @Valid UpdateAdminProfileDtoRequest request){
        try{
            User user = userDao.getBySession(cookieValue);
            String userType = userDao.getUserType(user);

            if(!userType.equals("admin")){
                throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
            }

            Admin admin = (Admin) user;

            String oldPassword = request.getOldPassword();

            if(!oldPassword.equals(user.getPassword())){
                throw new BusCompanyException(ErrorCode.INVALID_PASSWORD);
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
        } catch (BusCompanyException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

}
