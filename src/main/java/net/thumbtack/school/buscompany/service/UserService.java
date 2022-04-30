package net.thumbtack.school.buscompany.service;

import net.thumbtack.school.buscompany.dao.AdminDao;
import net.thumbtack.school.buscompany.dao.BaseDao;
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
import net.thumbtack.school.buscompany.exception.BusCompanyException;
import net.thumbtack.school.buscompany.exception.ErrorCode;
import net.thumbtack.school.buscompany.mapper.mapstruct.*;
import net.thumbtack.school.buscompany.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.text.ParseException;
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
            throw new BusCompanyException(ErrorCode.OFFLINE_OPERATION, "login");
        }

        User user = userDao.getByLogin(request.getLogin());
        String userType = user.getUserType();
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
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    userDao.closeSession(uuid);
                    LOGGER.info("User " + user.getLogin() + " disconnected");
                }
            };
            Timer timer = new Timer();
            timer.schedule(timerTask, cookieMaxAge * 1000);
        }).start();

        LOGGER.info("User " + user.getLogin() + " joined the server");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<LogoutUserDtoResponse> logoutUser(String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "logout");
        }
        User user = userDao.getBySession(cookieValue);

        userDao.closeSession(cookieValue);
        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " left the server");

        ResponseCookie cookie = deleteJavaSessionCookie(cookieValue);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new LogoutUserDtoResponse());
    }

    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "unregister");
        }

        User user = userDao.getBySession(cookieValue);
        userDao.unregister(user);

        ResponseCookie cookie = deleteJavaSessionCookie(cookieValue);

        LOGGER.info("User " + user.getLogin() + " unregistered");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.SET_COOKIE, cookie.toString()).body(
                new UnregisterUserDtoResponse()
        );
    }

    public GetProfileDtoResponse getUserProfile(String cookieValue) {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getProfile");
        }

        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

        GetProfileDtoResponse response;

        if(userType.equals("admin")){
            Admin admin = (Admin) user;
            response = AdminMapstructMapper.INSTANCE.toGetProfileDto(admin);
        }
        else{
            Client client = (Client) user;
            response = ClientMapstructMapper.INSTANCE.toGetProfileDto(client);
        }

        LOGGER.info("User " + user.getLogin() + " got his profile info");
        return response;
    }

    public GetOrdersDtoResponse getAllOrders(String cookieValue, String fromStation,
                                             String toStation, String busName,
                                             String fromDate, String toDate, String clientId)  {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION, "getAllOrders");
        }

        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

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
            orders.retainAll(userDao.getOrdersFromDate(parseDate(fromDate)));
        }

        if(toDate != null){
            orders.retainAll(userDao.getOrdersToDate(parseDate(toDate)));
        }

        if(userType.equals("admin")){

            if(clientId != null){
                orders.retainAll(userDao.getOrdersByClientId(Integer.parseInt(clientId)));
            }
        }
        else{
            orders.retainAll(userDao.getOrdersByClientId(user.getId()));
        }

        List<GetOrderDtoResponse> responseList = new ArrayList<>();
        for(Order order : orders){
            if(clientDao.isClientOrder(user, order) || userType.equals("admin")) {
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

        return new GetOrdersDtoResponse(responseList);
    }

    public GetSettingsDtoResponse getSettings(String cookieValue) {
        return new GetSettingsDtoResponse(
                maxNameLength, userIdleTimeout, minPasswordLength
        );
    }

    public GetTripsDtoResponse getAllTrips(String cookieValue, String fromStation, String toStation, String busName, String fromDate, String toDate) throws ParseException {
        if(cookieValue == null){
            throw new BusCompanyException(ErrorCode.ONLINE_OPERATION);
        }
        User user = userDao.getBySession(cookieValue);
        String userType = user.getUserType();

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
            trips.retainAll(userDao.getTripsFromDate(parseDate(fromDate)));
        }

        if(toDate != null){
            trips.retainAll(userDao.getTripsToDate(parseDate(toDate)));
        }

        List<GetTripDtoResponse> responseList = new ArrayList<>();

        if(userType.equals("admin")){
            for(Trip trip : trips){
                GetTripAdminDtoResponse getTripAdminDtoResponse;
                Bus bus = userDao.getBus(trip.getBusName());
                BusDtoResponse busDtoResponse = BusMapstructMapper.INSTANCE.toDtoResponse(bus);

                Schedule schedule = adminDao.getSchedule(trip);

                if(schedule != null){
                    ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);

                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse(trip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setSchedule(scheduleDtoResponse);
                    getTripAdminDtoResponse.setDates(formatDates(createDates(schedule)));
                }
                else{
                    getTripAdminDtoResponse = TripMapstructMapper.INSTANCE.toGetTripAdminDtoResponse(trip);
                    getTripAdminDtoResponse.setBus(busDtoResponse);
                    getTripAdminDtoResponse.setDates(formatDates(trip.getDates()));
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

                    Schedule schedule = adminDao.getSchedule(trip);

                    if (schedule != null) {
                        ScheduleDtoResponse scheduleDtoResponse = TripMapstructMapper.INSTANCE.toScheduleDtoResponse(schedule);

                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(trip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setSchedule(scheduleDtoResponse);
                        getTripDtoResponse.setDates(formatDates(createDates(schedule)));
                    }
                    else {
                        getTripDtoResponse = TripMapstructMapper.INSTANCE.toGetTripClientDtoResponse(trip);
                        getTripDtoResponse.setBus(busDtoResponse);
                        getTripDtoResponse.setDates(formatDates(trip.getDates()));
                    }

                    responseList.add(getTripDtoResponse);
                }
            }
        }
        LOGGER.info("User-" + user.getUserType() + " " + user.getLogin() + " got the information about trips with special params");

        return new GetTripsDtoResponse(responseList);
    }

    public ClearDatabaseDtoResponse clearAll() {
        LOGGER.info("Database cleared successfully!");
        baseDao.clearAll();
        return new ClearDatabaseDtoResponse();
    }
}
