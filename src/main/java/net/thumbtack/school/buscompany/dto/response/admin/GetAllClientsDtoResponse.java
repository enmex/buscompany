package net.thumbtack.school.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetAllClientsDtoResponse {
    private List<GetClientProfileDtoResponse> clients;
}
