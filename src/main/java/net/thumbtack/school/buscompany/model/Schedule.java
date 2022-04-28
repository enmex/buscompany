package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Schedule {
    private int tripId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String period;
}
