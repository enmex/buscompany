package net.thumbtack.school.buscompany.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DatesTrip extends Trip{
    private List<Date> dates;

    public DatesTrip(@NonNull String busName, @NonNull String fromStation,
                     @NonNull String toStation, @NonNull Date start,
                     @NonNull Date duration, @NonNull int price, List<Date> dates) {
        super(busName, fromStation, toStation, start, duration, price);
        this.dates = dates;
    }
}
