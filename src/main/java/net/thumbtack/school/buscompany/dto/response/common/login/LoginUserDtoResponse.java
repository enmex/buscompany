package net.thumbtack.school.buscompany.dto.response.common.login;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class LoginUserDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
}
