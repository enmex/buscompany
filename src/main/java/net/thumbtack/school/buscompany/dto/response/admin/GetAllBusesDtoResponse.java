package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@AllArgsConstructor
@Getter
public class GetAllBusesDtoResponse {
    List<BusDtoResponse> buses;
}
