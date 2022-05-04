package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.dto.response.common.profile.GetClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.profile.UpdateClientProfileDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.register.RegisterClientDtoResponse;
import net.thumbtack.school.buscompany.model.Client;
import net.thumbtack.school.buscompany.model.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import javax.validation.Valid;
import java.util.Locale;

@Mapper
public interface ClientMapstructMapper {

    ClientMapstructMapper INSTANCE = Mappers.getMapper(ClientMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userType", ignore = true)
    @Mapping(target = "orders", ignore = true)
    Client fromRegisterDto(@Valid RegisterClientDtoRequest request);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "toLowerCase")
    GetClientProfileDtoResponse toGetProfileDto(Client client);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "toLowerCase")
    RegisterClientDtoResponse toRegisterDto(Client client);

    @Mapping(source = "userType", target = "userType", qualifiedByName = "toLowerCase")
    UpdateClientProfileDtoResponse toUpdateDto(Client client);

    @Named("toLowerCase")
    default String toLowerCase(UserType type){
        return type.toString().toLowerCase(Locale.ROOT);
    }
}
