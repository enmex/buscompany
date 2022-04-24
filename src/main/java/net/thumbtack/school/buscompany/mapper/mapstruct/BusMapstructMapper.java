package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.admin.RegisterBusDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.RegisterBusDtoResponse;
import net.thumbtack.school.buscompany.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BusMapstructMapper {
    BusMapstructMapper INSTANCE = Mappers.getMapper(BusMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    Bus fromRegisterDto(RegisterBusDtoRequest request);

    BusDtoResponse toDtoResponse(Bus bus);

    RegisterBusDtoResponse toRegisterDto(Bus bus);
}
