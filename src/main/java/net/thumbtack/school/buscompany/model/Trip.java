package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
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
    private LocalTime start;
    @NonNull
    private LocalTime duration;
    private int price;
    private boolean approved;

    private List<LocalDate> dates;

    public Trip(int id, @NonNull String busName,
                @NonNull String fromStation, @NonNull String toStation,
                @NonNull LocalTime start, @NonNull LocalTime duration, int price, boolean approved) {
        this.id = id;
        this.busName = busName;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.start = start;
        this.duration = duration;
        this.price = price;
        this.approved = approved;
    }
}