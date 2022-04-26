package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class Trip {
    protected int id;
    @NonNull
    protected String busName;
    @NonNull
    protected String fromStation;
    @NonNull
    protected String toStation;
    @NonNull
    protected Date start;
    @NonNull
    protected Date duration;
    @NonNull
    protected int price;

    protected boolean approved;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trip)) return false;
        Trip trip = (Trip) o;
        return getId() == trip.getId() && getPrice() == trip.getPrice() && isApproved() == trip.isApproved()
                && getBusName().equals(trip.getBusName()) && getFromStation().equals(trip.getFromStation())
                && getToStation().equals(trip.getToStation()) && getStart().equals(trip.getStart())
                && getDuration().equals(trip.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBusName(), getFromStation(), getToStation(), getStart(), getDuration(), getPrice(), isApproved());
    }
}