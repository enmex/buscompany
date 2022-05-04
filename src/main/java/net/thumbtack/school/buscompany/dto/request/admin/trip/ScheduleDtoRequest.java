package net.thumbtack.school.buscompany.dto.request.admin.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.school.buscompany.validation.Date;
import net.thumbtack.school.buscompany.validation.Period;
import net.thumbtack.school.buscompany.validation.Schedule;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleDtoRequest {
    @NotNull
    @Date(style = "yyyy-MM-dd")
    private String fromDate;
    @NotNull
    @Date(style = "yyyy-MM-dd")
    private String toDate;
    @NotNull
    @Period
    private String period;
}
