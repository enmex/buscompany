package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class Passenger {
    private int id;
    private String firstName;
    private String lastName;
    private String passport;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger)) return false;
        Passenger passenger = (Passenger) o;
        boolean a = getId() == passenger.getId();
        boolean b = getFirstName().equals(passenger.getFirstName());
        boolean c = getLastName().equals(passenger.getLastName());
        boolean d = getPassport().equals(passenger.getPassport());
        boolean res = a && b && c && d;
        return getId() == passenger.getId() && getFirstName().equals(passenger.getFirstName())
                && getLastName().equals(passenger.getLastName()) && getPassport().equals(passenger.getPassport());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getPassport());
    }
}
