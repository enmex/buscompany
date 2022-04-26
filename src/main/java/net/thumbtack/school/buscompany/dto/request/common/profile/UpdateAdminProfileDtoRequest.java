package net.thumbtack.school.buscompany.dto.request.common.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class UpdateAdminProfileDtoRequest extends UpdateUserProfileDtoRequest{
    @NotNull
    private String position;

    public UpdateAdminProfileDtoRequest(@NotNull String firstName, @NotNull String lastName,
                                        @NotNull String patronymic, @NotNull String oldPassword,
                                        @NotNull String newPassword, String position) {
        super(firstName, lastName, patronymic, oldPassword, newPassword);
        this.position = position;
    }
}
