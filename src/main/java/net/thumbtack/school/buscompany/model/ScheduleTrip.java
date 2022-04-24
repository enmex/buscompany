package net.thumbtack.school.buscompany.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ScheduleTrip extends Trip{
    private Schedule schedule;

    public ScheduleTrip(@NonNull String busName, @NonNull String fromStation,
                        @NonNull String toStation, @NonNull Date start,
                        @NonNull Date duration, @NonNull int price, Schedule schedule) {
        super(busName, fromStation, toStation, start, duration, price);
        this.schedule = schedule;
    }
}
