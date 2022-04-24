package net.thumbtack.school.buscompany.dto.request.common.register;

import lombok.*;
import net.thumbtack.school.buscompany.validation.Name;
import net.thumbtack.school.buscompany.validation.Phone;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
public class RegisterClientDtoRequest {
    @NotNull
    @NonNull
    @Name
    private String firstName;
    @NotNull
    @NonNull
    private String lastName;
    private String patronymic;
    @NotNull
    @NonNull
    @Email
    private String email;
    @NotNull
    @NonNull
    @Phone
    private String phone;
    @NotNull
    @NonNull
    private String login;
    @NotNull
    @NonNull
    private String password;
}
