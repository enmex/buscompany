package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.*;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.BusMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.TripMapstructMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.service.DateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/trips")
public class TripController extends BaseController{
    private final UserDao userDao;
    private final AdminDao adminDao;

    private final DateService dateService;

    public TripController(UserDao userDao, AdminDao adminDao, DateService dateService){
        this.userDao = userDao;
        this.adminDao = adminDao;
        this.dateService = dateService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterTripDtoResponse> addTrip(
            @CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
            @RequestBody @Valid RegisterTripDtoRequest request){
        try{
            if(request.getSchedule() != null && request.getDates() != null){
                throw new BusCompanyException(ErrorCode.DATES_AND_SCHEDULE_INVOLVED);
            }

            User user = userDao.getBySession(cookieValue);
            String userType = userDao.getUserType(user);

            if(!userType.equals("admin")){
                throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
            }

            if(!adminDao.containsBus(request.getBusName())){
                adminDao.registerBus(new Bus(request.getBusName()));
            }

            RegisterTripDtoResponse response;
            Bus bus = adminDao.getBus(request.getBusName());
            BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

            if(request.getSchedule() != null) {

                Schedule schedule = TripMapstructMapper.INSTANCE.fromScheduleDtoRequest(request.getSchedule());
                ScheduleTrip trip = TripMapstructMapper.INSTANCE.fromRegisterScheduleTripDto(request);

                trip.setSchedule(schedule);

                adminDao.registerTrip(trip);

                response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
                response.setBus(busDtoResponse);

                ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.
                        INSTANCE.toScheduleDtoResponse(trip.getSchedule());

                response.setSchedule(scheduleDtoResponse);
                response.setDates(dateService.createDates(trip.getSchedule()));
            }
            else {

                DatesTrip trip = TripMapstructMapper.INSTANCE.fromRegisterDatesTripDto(request);

                adminDao.registerTrip(trip);

                response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
                response.setDates(dateService.createDates(trip.getDates()));
                response.setBus(busDtoResponse);
            }

            return ResponseEntity.ok().body(response);
        }
        catch(BusCompanyException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getErrorCode().toString());
        }
    }

    @PutMapping(value = "/{tripId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateTripDtoResponse> updateTrip(@CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                            @PathVariable String tripId, @RequestBody UpdateTripDtoRequest request){
        try{
            User user = userDao.getBySession(cookieValue);
            String userType = userDao.getUserType(user);

            if(!userType.equals("admin")){
                throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
            }

            Trip trip = adminDao.getTripById(tripId);

            if(trip == null){
                throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
            }

            if(trip instanceof ScheduleTrip){
                ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

                if(request.getSchedule() == null){
                    throw new BusCompanyException(ErrorCode.EMPTY_SCHEDULE);
                }

                adminDao.updateTrip(scheduleTrip);
            }
        } catch (BusCompanyException e) {
            e.printStackTrace();
        }
    }


    @DeleteMapping(value = "/{tripId}")
    public ResponseEntity<DeleteTripDtoResponse> deleteTrip(@CookieValue(name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                            @PathVariable String tripId){
        try{
            User user = userDao.getBySession(cookieValue);
            String userType = userDao.getUserType(user);

            if(!userType.equals("admin")){
                throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
            }

            Trip trip = adminDao.getTripById(tripId);


        } catch (BusCompanyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
