package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.*;

import java.util.List;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTripProfileDtoResponse {
    private int tripId;
    @NonNull
    private String fromStation;
    @NonNull
    private String toStation;
    @NonNull
    private String start;
    @NonNull
    private String duration;
    private int price;
    @NonNull
    private BusDtoResponse bus;
    private boolean approved;

    private ScheduleDtoResponse schedule;

    private List<String> dates;
}