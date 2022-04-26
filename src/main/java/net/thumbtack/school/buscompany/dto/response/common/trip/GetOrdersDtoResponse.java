package net.thumbtack.school.buscompany.dto.response.common.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetOrdersDtoResponse {
    private List<GetOrderDtoResponse> orders;
}
