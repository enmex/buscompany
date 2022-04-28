package net.thumbtack.school.buscompany.dto.response.common.register;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RegisterAdminDtoResponse extends RegisterUserDtoResponse{
    private String position;

    public RegisterAdminDtoResponse(int id, String firstName, String lastName,
                                    String patronymic, String userType, String position) {
        super(id, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
