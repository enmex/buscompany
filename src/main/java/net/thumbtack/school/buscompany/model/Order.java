package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {
    private int id;
    private int tripId;
    private Date date;
    private String fromStation;
    private String toStation;
    private String busName;
    private Date start;
    private Date duration;
    private int price;
    private int totalPrice;
    private List<Passenger> passengers;

    public boolean containsPassenger(Passenger passenger){
        return passengers.contains(passenger);
    }

    public void calculateTotalPrice(){
        totalPrice = passengers.size() * price;
    }

    public int getPassengersNumber(){
        return passengers.size();
    }

    public Passenger getPassenger(int i){
        return passengers.get(i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return getTripId() == order.getTripId() && getPrice() == order.getPrice()
                && getTotalPrice() == order.getTotalPrice() && getDate().equals(order.getDate())
                && getFromStation().equals(order.getFromStation()) && getToStation().equals(order.getToStation())
                && getBusName().equals(order.getBusName()) && getStart().equals(order.getStart())
                && getDuration().equals(order.getDuration()) && getPassengers().equals(order.getPassengers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTripId(), getDate(), getFromStation(),
                getToStation(), getBusName(), getStart(), getDuration(),
                getPrice(), getTotalPrice(), getPassengers());
    }
}
