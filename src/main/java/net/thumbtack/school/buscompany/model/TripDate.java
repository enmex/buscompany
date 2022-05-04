package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class TripDate {
    private int id;
    @NonNull
    private LocalDate date;
}
