package net.thumbtack.school.buscompany.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class DatesTrip extends Trip{
    private List<Date> dates;


    public DatesTrip(int id, @NonNull String busName,
                     @NonNull String fromStation, @NonNull String toStation,
                     @NonNull Date start, @NonNull Date duration,
                     @NonNull int price, boolean approved, List<Date> dates) {
        super(id, busName, fromStation, toStation, start, duration, price, approved);
        this.dates = dates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatesTrip)) return false;
        DatesTrip datesTrip = (DatesTrip) o;
        return getDates().equals(datesTrip.getDates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDates());
    }
}
