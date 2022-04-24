package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thumbtack.school.buscompany.model.Bus;
import java.util.List;

@AllArgsConstructor
@Getter
public class GetAllBusesDtoResponse {
    List<Bus> buses;
}
