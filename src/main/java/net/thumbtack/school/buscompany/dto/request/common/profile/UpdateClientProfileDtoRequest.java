package net.thumbtack.school.buscompany.dto.request.common.profile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.school.buscompany.validation.Phone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Setter
@Getter
public class UpdateClientProfileDtoRequest extends UpdateUserProfileDtoRequest{
    @NotNull
    @Email
    private String email;
    @NotNull
    @Phone
    private String phone;

    public UpdateClientProfileDtoRequest(@NotNull String firstName, @NotNull String lastName,
                                         @NotNull String patronymic, @NotNull String oldPassword,
                                         @NotNull String newPassword, String email, String phone) {
        super(firstName, lastName, patronymic, oldPassword, newPassword);
        this.email = email;
        this.phone = phone;
    }
}
