package net.thumbtack.school.buscompany.dto.response.common.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTripsDtoResponse {
    private List<GetTripDtoResponse> trips;
}
