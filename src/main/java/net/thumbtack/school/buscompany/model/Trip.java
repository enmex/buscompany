package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.util.Date;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public abstract class Trip {
    protected String id;
    @NonNull
    protected String busName;
    @NonNull
    protected String fromStation;
    @NonNull
    protected String toStation;
    @NonNull
    protected Date start;
    @NonNull
    protected Date duration;
    @NonNull
    protected int price;

    protected boolean approved;
}