package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BusDtoResponse {
    private String busName;
    private int placeCount;
}
