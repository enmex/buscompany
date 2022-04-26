package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.admin.RegisterBusDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.GetAllBusesDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterBusDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.BusMapstructMapper;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController extends BaseController{
    private final UserDao userDao;
    private final AdminDao adminDao;

    public BusController(UserDao userDao, AdminDao adminDao){
        this.userDao = userDao;
        this.adminDao = adminDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterBusDtoResponse> registerBus(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                              @RequestBody @Valid RegisterBusDtoRequest request){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Bus bus = BusMapstructMapper.INSTANCE.fromRegisterDto(request);
        adminDao.registerBus(bus);

        RegisterBusDtoResponse response = BusMapstructMapper.INSTANCE.toRegisterDto(bus);

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllBusesDtoResponse> getAllBuses(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getAllBuses");
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getAllBuses");
        }

        List<Bus> buses = adminDao.getAllBuses();

        return ResponseEntity.ok().body(
                new GetAllBusesDtoResponse(buses)
        );
    }
}
