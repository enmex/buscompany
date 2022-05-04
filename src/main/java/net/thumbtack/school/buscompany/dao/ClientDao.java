package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Place;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClientDao {
    void insertOrder(Order order);
    List<Integer> getFreeSeats(Order order);
    void changeSeat(Place place);
    void cancelOrder(int orderId);

    boolean isClientOrder(User user, Order order);

    int takeSeat(Place place);
    void insertPassenger(Order order, Passenger passenger);

    int takeSeats(Order order);

    int getIdTripDate(Order order);
}
