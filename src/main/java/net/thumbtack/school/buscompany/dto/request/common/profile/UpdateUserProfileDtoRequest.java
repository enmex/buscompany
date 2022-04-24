package net.thumbtack.school.buscompany.dto.request.common.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.thumbtack.school.buscompany.validation.Name;
import net.thumbtack.school.buscompany.validation.Password;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public abstract class UpdateUserProfileDtoRequest {
    @NotNull
    @Name
    private String firstName;
    @NotNull
    @Name
    private String lastName;
    @NotNull
    @Name
    private String patronymic;
    @NotNull
    private String oldPassword;
    @NotNull
    @Password
    private String newPassword;
}
