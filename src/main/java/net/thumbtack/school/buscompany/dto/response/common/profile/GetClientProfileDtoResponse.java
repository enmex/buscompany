package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.Getter;

@Getter
public class GetClientProfileDtoResponse extends GetProfileDtoResponse{
    private String email;
    private String phone;

    public GetClientProfileDtoResponse(int id, String firstName, String lastName,
                                       String patronymic, String userType, String email, String phone){
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phone = phone;
    }
}
