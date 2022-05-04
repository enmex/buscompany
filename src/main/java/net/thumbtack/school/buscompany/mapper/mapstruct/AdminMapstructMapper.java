package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateAdminProfileDtoResponse;
import net.thumbtack.school.buscompany.model.Admin;
import net.thumbtack.school.buscompany.model.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Locale;

@Mapper
public interface AdminMapstructMapper {

    AdminMapstructMapper INSTANCE = Mappers.getMapper(AdminMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userType", ignore = true)
    Admin fromRegisterDto(RegisterAdminDtoRequest request);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "toLowerCase")
    UpdateAdminProfileDtoResponse toUpdateDto(Admin admin);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "toLowerCase")
    GetAdminProfileDtoResponse toGetProfileDto(Admin admin);

    @Named("toLowerCase")
    default String toLowerCase(UserType type){
        return type.toString().toLowerCase(Locale.ROOT);
    }
}
