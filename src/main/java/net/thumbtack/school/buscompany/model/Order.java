package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    private int id;
    private Client client;
    private Trip trip;
    private List<Passenger> passengers;

    public int getPassengersNumber(){
        return passengers.size();
    }

    public Passenger getPassenger(int i){
        return passengers.get(i);
    }

    public boolean containsPassenger(Passenger passenger){
        return passengers.contains(passenger);
    }
}
