package net.thumbtack.school.buscompany.dto.request.admin.trip;

import lombok.*;
import net.thumbtack.school.buscompany.validation.Date;
import net.thumbtack.school.buscompany.validation.DatesOrSchedule;
import net.thumbtack.school.buscompany.validation.Schedule;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@DatesOrSchedule
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateTripDtoRequest {
    @NotNull
    @NonNull
    private String busName;
    @NotNull
    @NonNull
    private String fromStation;
    @NotNull
    @NonNull
    private String toStation;
    @NotNull
    @NonNull
    @Date
    private String start;
    @NotNull
    @NonNull
    @Date
    private String duration;

    @NonNull
    @Positive
    private int price;

    @Valid
    @Schedule
    private ScheduleDtoRequest schedule;

    @Date(style = "yyyy-MM-dd")
    private List<String> dates;
}
