package net.thumbtack.school.buscompany.dto.response.common.profile;

import lombok.Getter;

@Getter
public class GetAdminProfileDtoResponse extends GetProfileDtoResponse{
    private String position;

    public GetAdminProfileDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String position){
        super(id, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
