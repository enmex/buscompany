package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class UpdateUserProfileDtoResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}
