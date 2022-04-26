package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class ScheduleTrip extends Trip{
    private Schedule schedule;

    public ScheduleTrip(int id, @NonNull String busName,
                        @NonNull String fromStation, @NonNull String toStation,
                        @NonNull Date start, @NonNull Date duration,
                        @NonNull int price, boolean approved,
                        Date fromDate, Date toDate, String period) {
        super(id, busName, fromStation, toStation, start, duration, price, approved);
        this.schedule = new Schedule(id, fromDate, toDate, period);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleTrip)) return false;
        ScheduleTrip that = (ScheduleTrip) o;
        return getSchedule().equals(that.getSchedule());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchedule());
    }
}
