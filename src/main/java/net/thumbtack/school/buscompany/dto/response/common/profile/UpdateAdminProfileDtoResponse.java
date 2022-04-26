package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UpdateAdminProfileDtoResponse extends UpdateUserProfileDtoResponse{
    private String position;

    public UpdateAdminProfileDtoResponse(String firstName, String lastName, String patronymic, String userType, String position) {
        super(firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
