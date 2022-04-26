package net.thumbtack.school.buscompany.controller;

import net.thumbtack.school.buscompany.cookie.BusCompanyCookies;
import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.*;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripsDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.BusMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.TripMapstructMapper;
import net.thumbtack.school.buscompany.model.*;
import net.thumbtack.school.buscompany.service.DateService;
import net.thumbtack.school.buscompany.validation.Date;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

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
            @CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
            @RequestBody @Valid RegisterTripDtoRequest request){
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "addTrip");
        }

        if(request.getSchedule() != null && request.getDates() != null){
            throw new BusCompanyException(ErrorCode.DATES_AND_SCHEDULE_INVOLVED, "addTrip");
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
        Bus bus = userDao.getBus(request.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        if(request.getSchedule() != null) {

            Schedule schedule = TripMapstructMapper.INSTANCE.fromDtoRequest(request.getSchedule());
            ScheduleTrip trip = TripMapstructMapper.INSTANCE.fromDtoRequest(request);

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

    @PutMapping(value = "/{tripId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateTripDtoResponse> updateTrip(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                            @PathVariable("tripId") int tripId,
                                                            @RequestBody UpdateTripDtoRequest request) throws BusCompanyException {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);
        Bus bus = userDao.getBus(request.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        adminDao.updateTrip(trip);

        UpdateTripDtoResponse response;

        if(trip instanceof ScheduleTrip){
            ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

            if(request.getSchedule() == null){
                throw new BusCompanyException(ErrorCode.EMPTY_SCHEDULE);
            }

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(scheduleTrip.getSchedule());

            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(scheduleTrip);
            response.setBus(busDtoResponse);
            response.setSchedule(scheduleDtoResponse);
            response.setDates(dateService.createDates(scheduleTrip.getSchedule()));
        }
        else{
            DatesTrip datesTrip = (DatesTrip) trip;

            if(request.getDates() == null){
                throw new BusCompanyException(ErrorCode.EMPTY_DATES);
            }

            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(datesTrip);
            response.setBus(busDtoResponse);
            response.setDates(dateService.createDates(datesTrip.getDates()));
        }

        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{tripId}")
    public ResponseEntity<DeleteTripDtoResponse> deleteTrip(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                            @PathVariable("tripId") int tripId) throws BusCompanyException {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        adminDao.deleteTrip(trip);

        return ResponseEntity.ok().body(new DeleteTripDtoResponse());
    }

    @GetMapping(value = "/{tripId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GetTripProfileDtoResponse getTripInfo(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                                 @PathVariable("tripId") int tripId) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        GetTripProfileDtoResponse response;
        Bus bus = userDao.getBus(trip.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        if(trip instanceof ScheduleTrip){
            ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(scheduleTrip.getSchedule());

            response = TripMapstructMapper.INSTANCE.toGetDtoResponse(scheduleTrip);
            response.setBus(busDtoResponse);
            response.setSchedule(scheduleDtoResponse);
            response.setDates(dateService.createDates(scheduleTrip.getSchedule()));
        }
        else{
            DatesTrip datesTrip = (DatesTrip) trip;

            response = TripMapstructMapper.INSTANCE.toGetDtoResponse(datesTrip);
            response.setBus(busDtoResponse);
            response.setDates(dateService.createDates(datesTrip.getDates()));
        }

        return response;
    }

    @PostMapping(value = "/{tripId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApproveTripDtoResponse approveTrip(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                              @PathVariable("tripId") int tripId) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        trip.setApproved(true);

        adminDao.approveTrip(trip);

        ApproveTripDtoResponse response;
        Bus bus = userDao.getBus(trip.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        if(trip instanceof ScheduleTrip){
            ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(scheduleTrip.getSchedule());

            response = TripMapstructMapper.INSTANCE.toApproveDtoResponse(scheduleTrip);
            response.setBus(busDtoResponse);
            response.setSchedule(scheduleDtoResponse);
            response.setDates(dateService.createDates(scheduleTrip.getSchedule()));
        }
        else{
            DatesTrip datesTrip = (DatesTrip) trip;

            response = TripMapstructMapper.INSTANCE.toApproveDtoResponse(datesTrip);
            response.setBus(busDtoResponse);
            response.setDates(dateService.createDates(datesTrip.getDates()));
        }

        return response;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GetTripsDtoResponse getAllTrips(@CookieValue(required = false, name = BusCompanyCookies.JAVASESSIONID) String cookieValue,
                                           @RequestParam(required = false) String fromStation,
                                           @RequestParam(required = false) String toStation,
                                           @RequestParam(required = false) String busName,
                                           @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String fromDate,
                                           @RequestParam(required = false) @Date(style = "yyyy-MM-dd") @Valid String toDate) throws ParseException {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = userDao.getUserType(user);

        Set<Trip> trips = new HashSet<>(userDao.getAllTrips());

        if(fromStation != null){
            trips.retainAll(userDao.getTripsFromStation(fromStation));
        }

        if(toStation != null){
            trips.retainAll(userDao.getTripsToStation(toStation));
        }

        if(busName != null){
            trips.retainAll(userDao.getTripsByBus(busName));
        }

        if(fromDate != null){
            trips.retainAll(userDao.getTripsFromDate(dateService.parseDate(toDate)));
        }

        List<GetTripDtoResponse> responseList = new ArrayList<>();

        if(userType.equals("admin")){
            for(Trip trip : trips){
                GetTripAdminDtoResponse getTripAdminDtoResponse;
                Bus bus = userDao.getBus(trip.getBusName());
                BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

                if(trip instanceof ScheduleTrip){
                    ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

                    ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(scheduleTrip.getSchedule());

                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse(scheduleTrip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setSchedule(scheduleDtoResponse);
                    getTripAdminDtoResponse.setDates(dateService.createDates(scheduleTrip.getSchedule()));
                }
                else{
                    DatesTrip datesTrip = (DatesTrip) trip;

                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse((DatesTrip) trip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setDates(dateService.createDates(datesTrip.getDates()));
                }

                responseList.add(getTripAdminDtoResponse);
            }
        }
        else{
            for(Trip trip : trips){
                if(trip.isApproved()) {
                    GetTripDtoResponse getTripDtoResponse;
                    Bus bus = userDao.getBus(trip.getBusName());
                    BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

                    if (trip instanceof ScheduleTrip) {
                        ScheduleTrip scheduleTrip = (ScheduleTrip) trip;

                        ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(scheduleTrip.getSchedule());

                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(scheduleTrip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setSchedule(scheduleDtoResponse);
                        getTripDtoResponse.setDates(dateService.createDates(scheduleTrip.getSchedule()));
                    }
                    else {
                        DatesTrip datesTrip = (DatesTrip) trip;

                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(datesTrip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setDates(dateService.createDates(datesTrip.getDates()));
                    }

                    responseList.add(getTripDtoResponse);
                }
            }
        }

        return new GetTripsDtoResponse(responseList);
    }

}
