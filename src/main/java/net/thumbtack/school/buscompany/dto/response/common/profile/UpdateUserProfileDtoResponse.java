package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public abstract class UpdateUserProfileDtoResponse {
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}
