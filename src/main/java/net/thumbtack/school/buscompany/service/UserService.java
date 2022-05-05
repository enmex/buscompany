package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.ClientDao;
import net.thumbtack.school.buscompany.dao.UserDao;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.response.ClearDatabaseDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.ScheduleDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.PassengerDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginClientDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.login.LoginUserDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.logout.LogoutUserDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.settings.GetSettingsDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.*;
import net.thumbtack.school.buscompany.dto.response.common.unregister.UnregisterUserDtoResponse;
import net.thumbtack.school.buscompany.exception.ServerException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.*;
import net.thumbtack.school.buscompany.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class UserService extends ServiceBase{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${buscompany.user_idle_timeout}")
    private int userIdleTimeout;
    @Value("${buscompany.min_password_length}")
    private int minPasswordLength;
    @Value("${buscompany.max_name_length}")
    private int maxNameLength;

    protected UserService(UserDao userDao, AdminDao adminDao, ClientDao clientDao) {
        super(userDao, adminDao, clientDao);
    }

    public ResponseEntity<LoginUserDtoResponse> loginUser(String cookieValue, LoginDtoRequest request){
        if(cookieValue != null){
            throw new ServerException(ErrorCode.OFFLINE_OPERATION);
        }

        User user = userDao.getByLogin(request.getLogin());
        UserType userType = user.getUserType();

        if(!user.getPassword().equals(request.getPassword())){
            throw new ServerException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
        }

        String uuid = userDao.insertSession(user);
        ResponseCookie cookie = createJavaSessionIdCookie(uuid);

        LoginUserDtoResponse response;

        if(userType == UserType.ADMIN){
            Admin admin = (Admin) user;
            response = new LoginAdminDtoResponse(admin.getId(), admin.getFirstName(), admin.getLastName(),
                    admin.getPatronymic(), userType.toString().toLowerCase(Locale.ROOT), admin.getPosition());
        }
        else{
            Client client = (Client) user;
            response = new LoginClientDtoResponse(client.getId(), client.getFirstName(), client.getLastName(),
                    client.getPatronymic(), userType.toString().toLowerCase(Locale.ROOT), client.getEmail(), client.getPhone());
        }

        LOGGER.info("User " + user.getLogin() + " joined the server");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<LogoutUserDtoResponse> logoutUser(String cookieValue) {
        if(cookieValue == null){
            throw new ServerException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);

        userDao.closeSession(cookieValue);
        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " left the server");

        ResponseCookie cookie = deleteJavaSessionCookie(cookieValue);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new LogoutUserDtoResponse());
    }

    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(String cookieValue) {
        if(cookieValue == null){
            throw new ServerException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        userDao.unregister(user);

        ResponseCookie cookie = deleteJavaSessionCookie(cookieValue);

        LOGGER.info("User " + user.getLogin() + " unregistered");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                new UnregisterUserDtoResponse()
        );
    }

    public ResponseEntity<GetProfileDtoResponse> getUserProfile(String cookieValue) {
        if(cookieValue == null){
            throw new ServerException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        UserType userType = user.getUserType();

        GetProfileDtoResponse response;

        if(userType == UserType.ADMIN){
            Admin admin = (Admin) user;
            response = AdminMapstructMapper.INSTANCE.toGetProfileDto(admin);
        }
        else{
            Client client = (Client) user;
            response = ClientMapstructMapper.INSTANCE.toGetProfileDto(client);
        }

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        LOGGER.info("User " + user.getLogin() + " got his profile info");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<GetOrdersDtoResponse> getAllOrders(String cookieValue, String fromStation,
                                             String toStation, String busName,
                                             String fromDate, String toDate, String clientId)  {
        if(cookieValue == null){
            throw new ServerException(ErrorCode.ONLINE_OPERATION);
        }

        User user = userDao.getBySession(cookieValue);
        UserType userType = user.getUserType();

        Set<Order> orders = userDao.getAllOrders();

        if(fromStation != null){
            orders.retainAll(userDao.getOrdersFromStation(fromStation));
        }

        if(toStation != null){
            orders.retainAll(userDao.getOrdersToStation(toStation));
        }

        if(busName != null){
            orders.retainAll(userDao.getOrdersByBus(busName));
        }

        if(fromDate != null){
            try{
                LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException ex){
                throw new ServerException(ErrorCode.INVALID_DATE);
            }

            orders.retainAll(userDao.getOrdersFromDate(parseDate(fromDate)));
        }

        if(toDate != null){
            try{
                LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            catch (DateTimeParseException ex){
                throw new ServerException(ErrorCode.INVALID_DATE);
            }

            orders.retainAll(userDao.getOrdersToDate(parseDate(toDate)));
        }

        if(userType == UserType.ADMIN){

            if(clientId != null){
                try{
                    Integer.parseInt(clientId);
                }
                catch (NumberFormatException ex){
                    throw new ServerException(ErrorCode.INVALID_ID);
                }

                orders.retainAll(userDao.getOrdersByClientId(Integer.parseInt(clientId)));
            }
        }
        else{
            orders.retainAll(userDao.getOrdersByClientId(user.getId()));
        }

        List<GetOrderDtoResponse> responseList = new ArrayList<>();
        for(Order order : orders){
            if(clientDao.isClientOrder(user, order) || userType == UserType.ADMIN) {
                Trip trip = order.getTrip();

                GetOrderDtoResponse getOrderDtoResponse = OrderMapstructMapper.INSTANCE.toGetDtoResponse(order);
                getOrderDtoResponse.setTotalPrice(order.getPassengersNumber() * trip.getPrice());

                List<PassengerDtoResponse> passengers = new ArrayList<>();
                for (Passenger passenger : order.getPassengers()) {
                    PassengerDtoResponse passengerDtoResponse = OrderMapstructMapper.INSTANCE.toDtoResponse(passenger);
                    passengers.add(passengerDtoResponse);
                }
                getOrderDtoResponse.setPassengers(passengers);

                responseList.add(getOrderDtoResponse);
            }
        }

        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " got the information about orders with special params");

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetOrdersDtoResponse(responseList));
    }

    public ResponseEntity<GetSettingsDtoResponse> getSettings(String cookieValue) {
        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetSettingsDtoResponse(
                maxNameLength, userIdleTimeout, minPasswordLength
        ));
    }

    public ResponseEntity<GetTripsDtoResponse> getAllTrips(String cookieValue, String fromStation,
                                           String toStation, String busName,
                                           String fromDate, String toDate) {
        if(cookieValue == null){
            throw new ServerException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        UserType userType = user.getUserType();

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
            try {
                parseDate(fromDate);
            }
            catch (DateTimeParseException ex){
                throw new ServerException(ErrorCode.INVALID_DATE);
            }

            trips.retainAll(userDao.getTripsFromDate(parseDate(fromDate)));
        }

        if(toDate != null){
            try {
                parseDate(toDate);
            }
            catch (DateTimeParseException ex){
                throw new ServerException(ErrorCode.INVALID_DATE);
            }

            trips.retainAll(userDao.getTripsToDate(parseDate(toDate)));
        }

        List<GetTripDtoResponse> responseList = new ArrayList<>();

        if(userType == UserType.ADMIN){
            for(Trip trip : trips){
                GetTripAdminDtoResponse getTripAdminDtoResponse;
                Bus bus = trip.getBus();
                BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

                Schedule schedule = adminDao.getSchedule(trip);

                if(schedule != null){
                    ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);

                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse(trip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setSchedule(scheduleDtoResponse);

                    List<TripDate> tripDates = new ArrayList<>();
                    List<LocalDate> dates = createDates(schedule);

                    for(LocalDate localDate : dates){
                        tripDates.add(new TripDate(0, localDate));
                    }

                    getTripAdminDtoResponse.setDates(formatDates(tripDates));
                }
                else{
                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse(trip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setDates(formatDates(trip.getTripDates()));
                }

                responseList.add(getTripAdminDtoResponse);
            }
        }
        else{
            for(Trip trip : trips){
                if(trip.isApproved()) {
                    GetTripDtoResponse getTripDtoResponse;
                    Bus bus = trip.getBus();
                    BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

                    Schedule schedule = adminDao.getSchedule(trip);

                    if (schedule != null) {
                        ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);

                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(trip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setSchedule(scheduleDtoResponse);

                        List<TripDate> tripDates = new ArrayList<>();
                        List<LocalDate> dates = createDates(schedule);

                        for(LocalDate localDate : dates){
                            tripDates.add(new TripDate(0, localDate));
                        }

                        getTripDtoResponse.setDates(formatDates(tripDates));
                    }
                    else {
                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(trip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setDates(formatDates(trip.getTripDates()));
                    }

                    responseList.add(getTripDtoResponse);
                }
            }
        }
        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " got the information about trips with special params");

        ResponseCookie cookie = createJavaSessionIdCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetTripsDtoResponse(responseList));
    }

    public ClearDatabaseDtoResponse clearAll() {
        LOGGER.info("Database cleared successfully!");
        baseDao.clearAll();
        return new ClearDatabaseDtoResponse();
    }
}
