package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.OrderTicketDtoRequest;
import net.thumbtack.school.buscompany.dto.request.client.PassengerDtoRequest;
import net.thumbtack.school.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.OrderTicketDtoResponse;
import net.thumbtack.school.buscompany.dto.response.client.PassengerDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetOrderDtoResponse;
import net.thumbtack.school.buscompany.model.Order;
import net.thumbtack.school.buscompany.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Mapper
public interface OrderMapstructMapper {
    OrderMapstructMapper INSTANCE = Mappers.getMapper(OrderMapstructMapper.class);

    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "trip", ignore = true)
    @Mapping(source = "tripId", target = "id", ignore = true)
    Order fromDtoRequest(OrderTicketDtoRequest request);

    @Mapping(target = "id", ignore = true)
    Passenger fromDtoRequest(PassengerDtoRequest request);

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "trip.fromStation", target = "fromStation")
    @Mapping(source = "trip.toStation", target = "toStation")
    @Mapping(source = "trip.bus.busName", target = "busName")
    @Mapping(source = "trip.start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "trip.duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "trip.price", target = "price")
    OrderTicketDtoResponse toDtoResponse(Order order);

    @Mapping(source = "id", target = "orderId")
    @Mapping(target = "passengers", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(source = "trip.id", target = "tripId")
    @Mapping(source = "trip.fromStation", target = "fromStation")
    @Mapping(source = "trip.toStation", target = "toStation")
    @Mapping(source = "trip.bus.busName", target = "busName")
    @Mapping(source = "trip.start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "trip.duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "trip.price", target = "price")
    GetOrderDtoResponse toGetDtoResponse(Order order);

    PassengerDtoResponse toDtoResponse(Passenger passenger);
    @Mapping(target = "ticket", ignore = true)
    ChoosePlaceDtoResponse fromDtoRequest(ChoosePlaceDtoRequest request);

    @Mapping(target = "id", ignore = true)
    Passenger fromDtoRequestToPassenger(ChoosePlaceDtoRequest request);

    @Named("toDateYYYYMMDD")
    default Date toDateYYYYMMDD(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    @Named("toStringYYYYMMDD")
    default String toStringYYYYMMDD(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    @Named("toStringHHMM")
    default String toStringHHMM(LocalTime time){
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
