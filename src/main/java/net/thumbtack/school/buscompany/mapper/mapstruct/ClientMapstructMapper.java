package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import net.thumbtack.school.buscompany.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import javax.validation.Valid;

@Mapper
public interface ClientMapstructMapper {

    ClientMapstructMapper INSTANCE = Mappers.getMapper(ClientMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    Client fromRegisterDto(@Valid RegisterClientDtoRequest request);

    @Mapping(target = "userType", ignore = true)
    GetClientProfileDtoResponse toGetProfileDto(Client client);
}
