package net.thumbtack.school.buscompany.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetFreePlacesDtoResponse {
    List<Integer> places;
}
