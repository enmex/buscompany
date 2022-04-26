package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class Schedule {
    private int tripId;
    private Date fromDate;
    private Date toDate;
    private String period;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schedule)) return false;
        Schedule schedule = (Schedule) o;
        return getTripId() == schedule.getTripId() && getFromDate().equals(schedule.getFromDate())
                && getToDate().equals(schedule.getToDate()) && getPeriod().equals(schedule.getPeriod());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTripId(), getFromDate(), getToDate(), getPeriod());
    }
}
