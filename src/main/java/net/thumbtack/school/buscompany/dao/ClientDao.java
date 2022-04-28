package net.thumbtack.school.buscompany.dao;

import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ClientDao {
    void insertOrder(int clientId, Order ticket);
    List<Integer> getOccupiedSeats(int tripId);
    Passenger getByPassport(String passport);
    void changeSeat(Passenger passenger, String placeNumber);
    void cancelOrder(int orderId);

    boolean isClientOrder(User user, Order order);

    void takeSeat(Order order, Passenger passenger, int seatNumber);
}
