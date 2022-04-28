package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Trip {
    private int id;
    @NonNull
    private String busName;
    @NonNull
    private String fromStation;
    @NonNull
    private String toStation;
    @NonNull
    private LocalDate start;
    @NonNull
    private LocalDate duration;
    private int price;

    private boolean approved;

    private List<LocalDate> dates;
}