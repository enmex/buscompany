package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// REVU посмотрите аннотацию @Data, и писать придется намного меньше
// Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode.
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Trip {
    private int id;
    @NonNull
    // REVU У Вас же есть class Bus, поэтому должно быть
    // private Bus bus
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

    // REVU нет, не List<LocalDate>, а List<TripDate>
    // сделайте этот класс
    // В нем, конечно, LocalDate, а может, что-то еще
    // Trip - это маршрут.
    // TripDate - это его имплементация в указанный день
    // например, если понадобится добавить ФИО водителя, то это туда - водители в разные дни разные
    // поэтому лучше свой класс
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