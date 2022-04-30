package net.thumbtack.school.buscompany.mapper.mapstruct;

import net.thumbtack.school.buscompany.dto.request.admin.trip.RegisterTripDtoRequest;
import net.thumbtack.school.buscompany.dto.request.admin.trip.ScheduleDtoRequest;
import net.thumbtack.school.buscompany.dto.response.admin.*;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripAdminDtoResponse;
import net.thumbtack.school.buscompany.dto.response.common.trip.GetTripDtoResponse;
import net.thumbtack.school.buscompany.model.Schedule;
import net.thumbtack.school.buscompany.model.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Mapper
public interface TripMapstructMapper {
    TripMapstructMapper INSTANCE = Mappers.getMapper(TripMapstructMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dates", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toDateHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toDateHHMM")
    @Mapping(target = "approved", ignore = true)
    Trip fromDtoRequest(RegisterTripDtoRequest request);

    @Mapping(target = "tripId", ignore = true)
    @Mapping(source = "fromDate", target = "fromDate", qualifiedByName = "toDateYYYYMMDD")
    @Mapping(source = "toDate", target = "toDate", qualifiedByName = "toDateYYYYMMDD")
    Schedule fromDtoRequest(ScheduleDtoRequest request);

    ScheduleDtoResponse toScheduleDtoResponse(ScheduleDtoRequest schedule);

    @Mapping(source = "fromDate", target = "fromDate", qualifiedByName = "toStringYYYYMMDD")
    @Mapping(source = "toDate", target = "toDate", qualifiedByName = "toStringYYYYMMDD")
    ScheduleDtoResponse toScheduleDtoResponse(Schedule schedule);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    RegisterTripDtoResponse toRegisterDtoResponse(Trip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    UpdateTripDtoResponse toUpdateDtoResponse(Trip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    GetTripProfileDtoResponse toGetDtoResponse(Trip trip);


    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    ApproveTripDtoResponse toApproveDtoResponse(Trip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    GetTripAdminDtoResponse toGetTripAdminDtoResponse(Trip trip);

    @Mapping(source = "id", target = "tripId")
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(source = "start", target = "start", qualifiedByName = "toStringHHMM")
    @Mapping(source = "duration", target = "duration", qualifiedByName = "toStringHHMM")
    @Mapping(source = "dates", target = "dates", qualifiedByName = "toStringListYYYYMMDD")
    GetTripDtoResponse toGetTripClientDtoResponse(Trip trip);


    @Named("toDateList")
    default List<LocalDate> toDateList(List<String> dates) {
        List<LocalDate> datesArray = new ArrayList<>(dates.size());

        for(String date : dates){
            datesArray.add(LocalDate.parse(date));
        }

        return datesArray;
    }

    @Named("toStringList")
    default List<String> toStringList(List<LocalDate> dates){
        List<String> datesList = new ArrayList<>(dates.size());

        for(LocalDate date : dates){
            datesList.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        return datesList;
    }

    @Named("toStringListYYYYMMDD")
    default List<String> toStringListYYYYMMDD(List<LocalDate> dates){
        List<String> datesStrings = new ArrayList<>(dates.size());

        for(LocalDate date : dates){
            datesStrings.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }

        return datesStrings;
    }

    @Named("toDateHHMM")
    default LocalTime toDateHHMM(String date) {
        return LocalTime.parse(date, DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Named("toStringHHMM")
    default String toStringHHMM(LocalTime date){
        return date.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Named("toDateYYYYMMDD")
    default LocalDate toDateYYYYMMDD(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @Named("toStringYYYYMMDD")
    default String toStringYYYYMMDD(LocalDate date){
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}

