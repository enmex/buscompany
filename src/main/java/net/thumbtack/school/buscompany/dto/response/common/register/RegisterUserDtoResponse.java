package net.thumbtack.school.buscompany.dto.response.common.register;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class RegisterUserDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}
