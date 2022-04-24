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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController extends BaseController{
    private UserDao userDao;
    private AdminDao adminDao;

    public BusController(UserDao userDao, AdminDao adminDao){
        this.userDao = userDao;
        this.adminDao = adminDao;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterBusDtoResponse> registerBus(@CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                              @RequestBody @Valid RegisterBusDtoRequest request){
        try{
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
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllBusesDtoResponse> getAllBuses(@CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue){
        try{
            User user = userDao.getBySession(cookieValue);
            String userType = userDao.getUserType(user);

            if(!userType.equals("admin")){
                throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
            }

            List<Bus> buses = adminDao.getAllBuses();

            return ResponseEntity.ok().body(
                    new GetAllBusesDtoResponse(buses)
            );
        }
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }
}
