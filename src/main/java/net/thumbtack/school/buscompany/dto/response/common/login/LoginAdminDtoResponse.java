package net.thumbtack.school.buscompany.dto.response.common.login;

import lombok.Getter;

@Getter
public class LoginAdminDtoResponse extends LoginUserDtoResponse{
    private String position;

    public LoginAdminDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String position) {
        super(id, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
