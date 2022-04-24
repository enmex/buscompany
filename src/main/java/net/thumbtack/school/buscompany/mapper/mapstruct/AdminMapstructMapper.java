package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.model.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AdminMapstructMapper {

    AdminMapstructMapper INSTANCE = Mappers.getMapper(AdminMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    Admin fromRegisterDto(RegisterAdminDtoRequest request);
    
}
