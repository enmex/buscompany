package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.Getter;

@Getter
public class UpdateClientProfileDtoResponse extends UpdateUserProfileDtoResponse{
    private String email;
    private String phone;

    public UpdateClientProfileDtoResponse(String firstName, String lastName, String patronymic, String userType, String email, String phone) {
        super(firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = phone;
    }
}
