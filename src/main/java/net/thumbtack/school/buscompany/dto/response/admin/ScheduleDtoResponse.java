package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScheduleDtoResponse {
    private String fromDate;
    private String toDate;
    private String period;
}
