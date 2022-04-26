package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public abstract class GetProfileDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;

    @Setter
    private String userType;
}
