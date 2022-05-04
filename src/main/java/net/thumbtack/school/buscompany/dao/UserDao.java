package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.exception.CheckedException;
import net.thumbtack.school.buscompany.model.Bus;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Trip;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
public interface UserDao {
    String register(User user) throws CheckedException;
    void unregister(User user) throws CheckedException;
    User getByLogin(String login) throws CheckedException;
    User getBySession(String uuid) throws CheckedException;
    String insertSession(User user) throws CheckedException;
    void closeSession(String uuid) throws CheckedException;
    String getUserType(User user) throws CheckedException;
    void updateUser(User user) throws CheckedException;
    Trip getTripById(int tripId) throws CheckedException;
    List<Trip> getTripsFromStation(String fromStation);
    List<Trip> getTripsToStation(String toStation);
    List<Trip> getTripsByBus(String busName);
    List<Trip> getTripsFromDate(LocalDate fromDate);
    List<Trip> getTripsToDate(LocalDate toDate);
    List<Trip> getAllTrips();
    Set<Order> getAllOrders();
    List<Order> getOrdersFromStation(String fromStation);
    List<Order> getOrdersToStation(String toStation);
    List<Order> getOrdersByBus(String busName);
    List<Order> getOrdersFromDate(LocalDate fromDate);
    List<Order> getOrdersToDate(LocalDate toDate);
    List<Order> getOrdersByClientId(int clientId);
    Order getOrderById(int orderId);
    Bus getBus(String busName) throws CheckedException;
    void updateSession(String uuid);
    void clearSessions(int userIdleTimeout);
}
