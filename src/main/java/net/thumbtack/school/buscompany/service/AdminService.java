package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BaseDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.admin.RegisterBusDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.UpdateTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.profile.UpdateAdminProfileDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.*;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateUserProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterAdminDtoResponse;
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.AdminMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.BusMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.ClientMapstructMapper;
import net.thumbtack.school.buscompany.mapper.mapstruct.TripMapstructMapper;
import net.thumbtack.school.buscompany.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class AdminService extends ServiceBase{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    public AdminService(UserDao userDao, AdminDao adminDao, ClientDao clientDao) {
        super(userDao, adminDao, clientDao);
    }

    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(String cookieValue, RegisterAdminDtoRequest request) {
        if(cookieValue != null){
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION, "register");
        }

        Admin admin = AdminMapstructMapper.INSTANCE.fromRegisterDto(request);
        admin.setUserType("admin");
        String uuid = userDao.register(admin);

        ResponseCookie cookie = createJavaSessionIdCookie(uuid);

        new Thread(() -> {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    userDao.closeSession(uuid);
                    LOGGER.info("User " + admin.getLogin() + " disconnected");
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, cookieMaxAge * 1000);
        }).start();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                new RegisterAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                        admin.getPatronymic(), admin.getUserType(), admin.getPosition())
        );
    }

    public UpdateUserProfileDtoResponse updateAdminProfile(String cookieValue, UpdateAdminProfileDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

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

        return AdminMapstructMapper.INSTANCE.toUpdateDto(admin);
    }

    public RegisterBusDtoResponse registerBus(String cookieValue, RegisterBusDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Bus bus = BusMapstructMapper.INSTANCE.fromRegisterDto(request);
        adminDao.registerBus(bus);

        return BusMapstructMapper.INSTANCE.toRegisterDto(bus);
    }

    public GetAllBusesDtoResponse getAllBuses(String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getAllBuses");
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getAllBuses");
        }

        List<Bus> buses = adminDao.getAllBuses();

        return  new GetAllBusesDtoResponse(buses);
    }

    public GetAllClientsDtoResponse getAllClients(String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED, "getAllFields");
        }

        List<Client> clients = adminDao.getAllClients();
        List<GetClientProfileDtoResponse> clientProfiles = new ArrayList<>();

        for(Client client : clients){
            clientProfiles.add(ClientMapstructMapper.INSTANCE.toGetProfileDto(client));
        }

        return new GetAllClientsDtoResponse(clientProfiles);
    }

    public RegisterTripDtoResponse addTrip(String cookieValue, RegisterTripDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "addTrip");
        }

        if(request.getSchedule() != null && request.getDates() != null){
            throw new BusCompanyException(ErrorCode.DATES_AND_SCHEDULE_INVOLVED, "addTrip");
        }

        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        if(!adminDao.containsBus(request.getBusName())){
            adminDao.registerBus(new Bus(request.getBusName()));
        }

        RegisterTripDtoResponse response;
        Bus bus = userDao.getBus(request.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        Trip trip = TripMapstructMapper.INSTANCE.fromDtoRequest(request);

        if(request.getSchedule() != null) {
            Schedule schedule = TripMapstructMapper.INSTANCE.fromDtoRequest(request.getSchedule());
            trip.setDates(createDates(schedule));

            adminDao.registerTrip(trip);
            adminDao.registerSchedule(trip, schedule);

            response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
            response.setBus(busDtoResponse);

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(request.getSchedule());

            response.setSchedule(scheduleDtoResponse);
            response.setDates(formatDates(trip.getDates()));
        }
        else {
            adminDao.registerTrip(trip);

            response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
            response.setDates(formatDates(trip.getDates()));
            response.setBus(busDtoResponse);
        }

        return response;
    }

    public UpdateTripDtoResponse updateTrip(String cookieValue, int tripId, UpdateTripDtoRequest request) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        if(request.getSchedule() != null && request.getDates() != null){
            throw new BusCompanyException(ErrorCode.DATES_AND_SCHEDULE_INVOLVED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        Bus bus = userDao.getBus(request.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        UpdateTripDtoResponse response;

        if(request.getSchedule() != null){
            if(request.getSchedule() == null){
                throw new BusCompanyException(ErrorCode.EMPTY_SCHEDULE);
            }
            Schedule schedule = TripMapstructMapper.INSTANCE.fromDtoRequest(request.getSchedule());

            trip.setDates(createDates(schedule));

            adminDao.updateSchedule(trip, schedule);

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(request.getSchedule());

            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(trip);
            response.setSchedule(scheduleDtoResponse);
        }
        else{
            if(request.getDates() == null){
                throw new BusCompanyException(ErrorCode.EMPTY_DATES);
            }

            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(trip);
        }
        response.setBus(busDtoResponse);
        response.setDates(formatDates(trip.getDates()));

        adminDao.updateTrip(trip);

        return response;
    }

    public DeleteTripDtoResponse deleteTrip(String cookieValue, int tripId) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }
        adminDao.deleteTrip(trip);

        return new DeleteTripDtoResponse();
    }

    public GetTripProfileDtoResponse getTripInfo(String cookieValue, int tripId) {
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

        response = TripMapstructMapper.INSTANCE.toGetDtoResponse(trip);
        response.setBus(busDtoResponse);
        response.setDates(formatDates(trip.getDates()));

        Schedule schedule = adminDao.getSchedule(trip);
        if(schedule != null){
            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);
            response.setSchedule(scheduleDtoResponse);
        }

        return response;
    }

    public ApproveTripDtoResponse approveTrip(String cookieValue, int tripId) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        if(!userType.equals("admin")){
            throw new BusCompanyException(ErrorCode.OPERATION_NOT_ALLOWED);
        }

        Trip trip = userDao.getTripById(tripId);

        if(trip == null){
            throw new BusCompanyException(ErrorCode.TRIP_NOT_EXISTS);
        }

        trip.setApproved(true);
        adminDao.approveTrip(trip);

        ApproveTripDtoResponse response = TripMapstructMapper.INSTANCE.toApproveDtoResponse(trip);;
        Bus bus = userDao.getBus(trip.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        response.setDates(formatDates(trip.getDates()));
        response.setBus(busDtoResponse);

        Schedule schedule = adminDao.getSchedule(trip);
        if(schedule != null){
            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);
            response.setSchedule(scheduleDtoResponse);
        }

        return response;
    }
}