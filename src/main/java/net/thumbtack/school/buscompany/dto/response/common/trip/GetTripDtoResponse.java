package net.thumbtack.school.buscompany.dto.response.common.trip;

import lombok.*;
import net.thumbtack.school.buscompany.dto.response.admin.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.ScheduleDtoResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTripDtoResponse {
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

    private BusDtoResponse bus;

    private ScheduleDtoResponse schedule;

    private List<String> dates;
}
