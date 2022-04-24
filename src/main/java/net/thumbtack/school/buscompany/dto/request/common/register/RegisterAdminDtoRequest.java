package net.thumbtack.school.buscompany.dto.request.common.register;

import lombok.*;
import net.thumbtack.school.buscompany.validation.Name;
import net.thumbtack.school.buscompany.validation.Password;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterAdminDtoRequest {
    @NotNull
    @Name
    private String firstName;
    @NotNull
    @Name
    private String lastName;
    @Name
    private String patronymic;
    @NotNull
    private String position;
    @NotNull
    private String login;
    @NotNull
    @Password
    private String password;
}
