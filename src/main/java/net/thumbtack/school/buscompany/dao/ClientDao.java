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
    List<Integer> getFreePlaces(Order order);
    void changePlace(Place place);
    void cancelOrder(int orderId);

    boolean isClientOrder(User user, Order order);

    int takePlace(Place place);
    void insertPassenger(Order order, Passenger passenger);

    int getIdTripDate(Order order);
}
