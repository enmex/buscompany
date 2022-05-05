package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
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
import net.thumbtack.school.buscompany.exception.ServerException;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminService extends ServiceBase{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    public AdminService(UserDao userDao, AdminDao adminDao, ClientDao clientDao) {
        super(userDao, adminDao, clientDao);
    }

    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(RegisterAdminDtoRequest request) {
        Admin admin = AdminMapstructMapper.INSTANCE.fromRegisterDto(request);
        admin.setUserType(UserType.ADMIN);
        String uuid = userDao.register(admin);

        ResponseCookie cookie = createJavaSessionIdCookie(uuid);
        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " just registered");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                new RegisterAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                        admin.getPatronymic(), admin.getUserType().toString().toLowerCase(Locale.ROOT), admin.getPosition())
        );
    }

    public ResponseEntity<UpdateUserProfileDtoResponse> updateAdminProfile(String cookieValue, UpdateAdminProfileDtoRequest request) {
        Admin admin = getAdmin(cookieValue);

        String oldPassword = request.getOldPassword();

        if(!oldPassword.equals(admin.getPassword())){
            throw new ServerException(ErrorCode.INVALID_PASSWORD);
        }

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPatronymic(request.getPatronymic());
        admin.setPassword(request.getNewPassword());
        admin.setPosition(request.getPosition());

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " just updated his profile");
        userDao.updateUser(admin);

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(AdminMapstructMapper.INSTANCE.toUpdateDto(admin));
    }

    public ResponseEntity<RegisterBusDtoResponse> registerBus(String cookieValue, RegisterBusDtoRequest request) {
        Admin admin = getAdmin(cookieValue);

        Bus bus = BusMapstructMapper.INSTANCE.fromRegisterDto(request);

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " registered new bus (" + bus.getBusName() + ")");
        adminDao.registerBus(bus);

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(BusMapstructMapper.INSTANCE.toRegisterDto(bus));
    }

    public ResponseEntity<GetAllBusesDtoResponse> getAllBuses(String cookieValue) {
        Admin admin = getAdmin(cookieValue);

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " got all buses info's");
        List<Bus> buses = adminDao.getAllBuses();
        List<BusDtoResponse> responseBusList = new ArrayList<>(buses.size());

        for(Bus bus : buses){
            responseBusList.add(BusMapstructMapper.INSTANCE.toDtoResponse(bus));
        }

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetAllBusesDtoResponse(responseBusList));
    }

    public ResponseEntity<GetAllClientsDtoResponse> getAllClients(String cookieValue) {
        Admin admin = getAdmin(cookieValue);

        List<Client> clients = adminDao.getAllClients();
        List<GetClientProfileDtoResponse> clientProfiles = new ArrayList<>();

        for(Client client : clients){
            clientProfiles.add(ClientMapstructMapper.INSTANCE.toGetProfileDto(client));
        }
        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " got all clients info`s");

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetAllClientsDtoResponse(clientProfiles));
    }

    public ResponseEntity<RegisterTripDtoResponse> addTrip(String cookieValue, RegisterTripDtoRequest request) {
        Admin admin = getAdmin(cookieValue);

        if(!adminDao.containsBus(request.getBusName())){
            Bus bus = new Bus();
            bus.setBusName(request.getBusName());
            adminDao.registerBus(bus);
        }

        RegisterTripDtoResponse response;
        Bus bus = userDao.getBus(request.getBusName());
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        Trip trip = TripMapstructMapper.INSTANCE.fromDtoRequest(request);
        trip.setBus(bus);

        if(request.getSchedule() != null) {
            Schedule schedule = TripMapstructMapper.INSTANCE.fromDtoRequest(request.getSchedule());

            List<TripDate> tripDates = new ArrayList<>();
            List<LocalDate> dates = createDates(schedule);

            for(LocalDate localDate : dates){
                tripDates.add(new TripDate(0, localDate));
            }

            trip.setTripDates(tripDates);

            adminDao.registerTrip(trip);
            adminDao.registerSchedule(trip, schedule);

            response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
            response.setBus(busDtoResponse);

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(request.getSchedule());

            response.setSchedule(scheduleDtoResponse);
            response.setDates(formatDates(trip.getTripDates()));
        }
        else {
            trip.setTripDates(parseDates(request.getDates()));
            adminDao.registerTrip(trip);

            response = TripMapstructMapper.INSTANCE.toRegisterDtoResponse(trip);
            response.setDates(formatDates(trip.getTripDates()));
            response.setBus(busDtoResponse);
        }

        adminDao.registerPlaces(trip, bus);

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " added new trip from "
                + trip.getFromStation() + " to " + trip.getToStation());
        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<UpdateTripDtoResponse> updateTrip(String cookieValue, String tripId, UpdateTripDtoRequest request) {
        int idTrip;

        try {
            idTrip = Integer.parseInt(tripId);
        }
        catch (NumberFormatException ex){
            throw new ServerException(ErrorCode.INVALID_ID);
        }

        Admin admin = getAdmin(cookieValue);

        Trip trip = userDao.getTripById(idTrip);
        Schedule scheduleTrip = adminDao.getSchedule(trip);

        if(trip == null){
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        if(!adminDao.containsBus(request.getBusName())){
            Bus bus = new Bus();
            bus.setBusName(request.getBusName());
            adminDao.registerBus(bus);
        }

        Bus bus = userDao.getBus(request.getBusName());

        trip.setBus(bus);
        trip.setPrice(request.getPrice());
        trip.setFromStation(request.getFromStation());
        trip.setToStation(request.getToStation());
        trip.setStart(parseTime(request.getStart()));
        trip.setDuration(parseTime(request.getDuration()));

        if(request.getDates() != null){
            if(scheduleTrip != null){
                throw new ServerException(ErrorCode.TRIP_IS_REGULAR);
            }

            trip.setTripDates(parseDates(request.getDates()));
        }

        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        UpdateTripDtoResponse response;

        if(request.getSchedule() != null){
            Schedule schedule = TripMapstructMapper.INSTANCE.fromDtoRequest(request.getSchedule());

            List<TripDate> tripDates = new ArrayList<>();
            List<LocalDate> dates = createDates(schedule);

            for(LocalDate localDate : dates){
                tripDates.add(new TripDate(0, localDate));
            }

            trip.setTripDates(tripDates);

            adminDao.updateSchedule(trip, schedule);

            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(request.getSchedule());

            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(trip);
            response.setSchedule(scheduleDtoResponse);
        }
        else{
            response = TripMapstructMapper.INSTANCE.toUpdateDtoResponse(trip);
        }
        response.setBus(busDtoResponse);
        response.setDates(formatDates(trip.getTripDates()));

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " updated trip from "
                + trip.getFromStation() + " to " + trip.getToStation());
        adminDao.updateTrip(trip);

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<DeleteTripDtoResponse> deleteTrip(String cookieValue, String tripId) {
        int idTrip;

        try {
            idTrip = Integer.parseInt(tripId);
        }
        catch (NumberFormatException ex){
            throw new ServerException(ErrorCode.INVALID_ID);
        }

        Admin admin = getAdmin(cookieValue);

        Trip trip = userDao.getTripById(idTrip);

        if(trip == null){
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " deleted trip from "
                + trip.getFromStation() + " to " + trip.getToStation());
        adminDao.deleteTrip(trip);

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new DeleteTripDtoResponse());
    }

     public ResponseEntity<GetTripProfileDtoResponse> getTripInfo(String cookieValue, String tripId) {
         int idTrip;

         try {
             idTrip = Integer.parseInt(tripId);
         }
         catch (NumberFormatException ex){
             throw new ServerException(ErrorCode.INVALID_ID);
         }

        Admin admin = getAdmin(cookieValue);

        Trip trip = userDao.getTripById(idTrip);

        if(trip == null){
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        GetTripProfileDtoResponse response;
        Bus bus = trip.getBus();
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        response = TripMapstructMapper.INSTANCE.toGetDtoResponse(trip);
        response.setBus(busDtoResponse);
        response.setDates(formatDates(trip.getTripDates()));

        Schedule schedule = adminDao.getSchedule(trip);
        if(schedule != null){
            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);
            response.setSchedule(scheduleDtoResponse);
        }

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " got info of the trip from "
                + trip.getFromStation() + " to " + trip.getToStation());
        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<ApproveTripDtoResponse> approveTrip(String cookieValue, String tripId) {
        int idTrip;

        try {
            idTrip = Integer.parseInt(tripId);
        }
        catch (NumberFormatException ex){
            throw new ServerException(ErrorCode.INVALID_ID);
        }

        Admin admin = getAdmin(cookieValue);

        Trip trip = userDao.getTripById(idTrip);

        if(trip == null){
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        trip.setApproved(true);
        adminDao.approveTrip(trip);

        ApproveTripDtoResponse response = TripMapstructMapper.INSTANCE.toApproveDtoResponse(trip);;
        Bus bus = trip.getBus();
        BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

        response.setDates(formatDates(trip.getTripDates()));
        response.setBus(busDtoResponse);

        Schedule schedule = adminDao.getSchedule(trip);
        if(schedule != null){
            ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);
            response.setSchedule(scheduleDtoResponse);
        }

        LOGGER.info("User-" + admin.getUserType() + " " + admin.getLogin() + " approved trip from "
                + trip.getFromStation() + " to " + trip.getToStation());

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }
}
