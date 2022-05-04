package net.thumbtack.school.buscompany.dto.request.admin.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.school.buscompany.validation.Date;
import net.thumbtack.school.buscompany.validation.DatesOrSchedule;
import net.thumbtack.school.buscompany.validation.Schedule;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import java.util.List;

@DatesOrSchedule
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterTripDtoRequest {
    @NotNull
    private String busName;
    @NotNull
    private String fromStation;
    @NotNull
    private String toStation;
    @NotNull
    @Date
    private String start;
    @NotNull
    @Date
    private String duration;
    @NotNull
    @Positive
    private int price;

    @Valid
    @Schedule
    private ScheduleDtoRequest schedule;

    @Date(style = "yyyy-MM-dd")
    private List<String> dates;
}
