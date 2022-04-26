package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTripProfileDtoResponse {
    @NonNull
    private int tripId;
    @NonNull
    private String fromStation;
    @NonNull
    private String toStation;
    @NonNull
    private String start;
    @NonNull
    private String duration;
    @NonNull
    private int price;
    @NonNull
    private BusDtoResponse bus;
    @NonNull
    private boolean approved;

    private ScheduleDtoResponse schedule;

    private List<String> dates;
}