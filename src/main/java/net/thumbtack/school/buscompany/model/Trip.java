package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class Trip {
    private int id;
    @NonNull
    private Bus bus;
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

    private List<TripDate> tripDates;

    public Trip(int id){
        this.id = id;
    }
}