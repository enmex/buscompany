package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.*;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripDtoResponse;
import net.thumbtack.school.buscompany.model.DatesTrip;
import net.thumbtack.school.buscompany.model.Schedule;
import net.thumbtack.school.buscompany.model.ScheduleTrip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mapper
public interface TripMapstructMapper {
    TripMapstructMapper INSTANCE = Mappers.getMapper(TripMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toDateHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toDateHHMM")
    @Mapping(target = "approved", ignore = true)
    ScheduleTrip fromDtoRequest(RegisterTripDtoRequest request);

    @Mapping(source = "fromDate", target = "fromDate", qualifiedByName = "toDateYYYYMMDD")
    @Mapping(source = "toDate", target = "toDate", qualifiedByName = "toDateYYYYMMDD")
    Schedule fromDtoRequest(ScheduleDtoRequest request);

    @Mapping(source = "fromDate", target = "fromDate", qualifiedByName = "toStringYYYYMMDD")
    @Mapping(source = "toDate", target = "toDate", qualifiedByName = "toStringYYYYMMDD")
    ScheduleDtoResponse toScheduleDtoResponse(Schedule schedule);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    RegisterTripDtoResponse toRegisterDtoResponse(ScheduleTrip trip);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toDateHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toDateHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toDateList")
    @Mapping(target = "approved", ignore = true)
    DatesTrip fromRegisterDatesTripDto(RegisterTripDtoRequest request);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    RegisterTripDtoResponse toRegisterDtoResponse(DatesTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    UpdateTripDtoResponse toUpdateDtoResponse(ScheduleTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    UpdateTripDtoResponse toUpdateDtoResponse(DatesTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripProfileDtoResponse toGetDtoResponse(ScheduleTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripProfileDtoResponse toGetDtoResponse(DatesTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    ApproveTripDtoResponse toApproveDtoResponse(ScheduleTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    ApproveTripDtoResponse toApproveDtoResponse(DatesTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripAdminDtoResponse toGetTripAdminDtoResponse(ScheduleTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripAdminDtoResponse toGetTripAdminDtoResponse(DatesTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripDtoResponse toGetTripClientDtoResponse(ScheduleTrip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringList")
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    GetTripDtoResponse toGetTripClientDtoResponse(DatesTrip trip);

    @Named("toDateList")
    default List<Date> toDateList(List<String> dates) throws ParseException {
        List<Date> datesArray = new ArrayList<>(dates.size());

        for(String date : dates){
            datesArray.add(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        }

        return datesArray;
    }

    @Named("toStringList")
    default List<String> toStringList(List<Date> dates){
        List<String> datesList = new ArrayList<>(dates.size());

        for(Date date : dates){
            datesList.add(new SimpleDateFormat("HH:mm").format(date));
        }

        return datesList;
    }

    @Named("toDateHHMM")
    default Date toDateHHMM(String date) throws ParseException {
        return new SimpleDateFormat("HH:mm").parse(date);
    }

    @Named("toStringHHMM")
    default String toStringHHMM(Date date){
        return new SimpleDateFormat("HH:mm").format(date);
    }

    @Named("toDateYYYYMMDD")
    default Date toDateYYYYMMDD(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    @Named("toStringYYYYMMDD")
    default String toStringYYYYMMDD(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

}

