package net.thumbtack.school.buscompany.dto.response.common.register;

import lombok.Getter;

@Getter
public class RegisterClientDtoResponse extends RegisterUserDtoResponse{
    private String email;
    private String phone;

    public RegisterClientDtoResponse(String id, String firstName, String lastName,
                                     String patronymic, String userType, String email, String phone) {
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = phone;
    }
}
