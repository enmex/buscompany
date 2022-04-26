package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.Getter;

@Getter
public class GetAdminProfileDtoResponse extends GetProfileDtoResponse{
    private String position;

    public GetAdminProfileDtoResponse(int id, String firstname, String lastname, String patronymic, String userType, String position){
        super(id, firstname, lastname, patronymic, userType);
        this.position = position;
    }
}
