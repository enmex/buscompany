package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    private int id;
    private Client client;
    private Trip trip;
    private LocalDate date;
    private List<Passenger> passengers;

    public Order(int id){
        this.id = id;
    }

    public int getPassengersNumber(){
        return passengers.size();
    }

    public boolean containsPassenger(Passenger passenger){
        return passengers.contains(passenger);
    }

    public Passenger getPassenger(String firstName, String lastName, String passport){
        for(Passenger passenger : passengers){
            if(passenger.getPassport().equals(passport)
                && passenger.getFirstName().equals(firstName)
                && passenger.getLastName().equals(lastName)){
                return passenger;
            }
        }
        return null;
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
    }

    public int getIdTripDate() {
        List<TripDate> tripDates = trip.getTripDates();

        for(TripDate date : tripDates) {
            if(date.getDate().equals(this.date)) {
                return date.getId();
            }
        }

        return -1;
    }
}
