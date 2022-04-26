package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class Ticket {
    private int id;
    private int tripId;
    private Date date;
    private List<Passenger> passengers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket)) return false;
        Ticket ticket = (Ticket) o;
        return getId() == ticket.getId() && getTripId() == ticket.getTripId()
                && getDate().equals(ticket.getDate()) && getPassengers().equals(ticket.getPassengers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTripId(), getDate(), getPassengers());
    }
}
