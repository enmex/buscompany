package net.thumbtack.school.buscompany.dto.request.common.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LoginDtoRequest {
    @NotNull
    private String login;
    @NotNull
    private String password;
}