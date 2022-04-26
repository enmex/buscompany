package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.client.ChooseSeatDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.ChooseSeatDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.PassengerDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrderDtoResponse;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import net.thumbtack.school.buscompany.model.Ticket;
import net.thumbtack.school.buscompany.validation.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper
public interface OrderMapstructMapper {
    OrderMapstructMapper INSTANCE = Mappers.getMapper(OrderMapstructMapper.class);

    @Mapping(target = "passengers", ignore = true)
    @Mapping(source = "date", target = "date", qualifiedByName = "toDateYYYYMMDD")
    Ticket fromDtoRequest(OrderTicketDtoRequest request);

    Passenger fromDtoRequest(PassengerDtoRequest request);

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "passengers", ignore = true)
    @Mapping(source = "date", target = "date", qualifiedByName = "toStringYYYYMMDD")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    OrderTicketDtoResponse toDtoResponse(Order order);

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "passengers", ignore = true)
    @Mapping(source = "date", target = "date", qualifiedByName = "toStringYYYYMMDD")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetOrderDtoResponse toGetDtoResponse(Order order);

    PassengerDtoResponse toDtoResponse(Passenger passenger);
    @Mapping(target = "ticket", ignore = true)
    ChooseSeatDtoResponse fromDtoRequest(ChooseSeatDtoRequest request);

    @Named("toDateYYYYMMDD")
    default Date toDateYYYYMMDD(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    @Named("toStringYYYYMMDD")
    default String toStringYYYYMMDD(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    @Named("toStringHHMM")
    default String toStringHHMM(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }
}
