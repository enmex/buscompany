package net.thumbtack.school.buscompany.dto.response.common.login;

import lombok.Getter;

@Getter
public class LoginClientDtoResponse extends LoginUserDtoResponse{
    private String email;
    private String phone;

    public LoginClientDtoResponse(String id, String firstName, String lastName, String patronymic, String userType, String email, String phone) {
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = phone;
    }
}
