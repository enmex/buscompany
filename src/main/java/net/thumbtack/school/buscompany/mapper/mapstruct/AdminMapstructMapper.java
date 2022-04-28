package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapstructMapper {

    AdminMapstructMapper INSTANCE = Mappers.getMapper(AdminMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userType", ignore = true)
    Admin fromRegisterDto(RegisterAdminDtoRequest request);

    UpdateAdminProfileDtoResponse toUpdateDto(Admin admin);

    GetAdminProfileDtoResponse toGetProfileDto(Admin admin);

}
