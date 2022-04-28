package net.thumbtack.school.buscompany.dto.response.common.trip;

import lombok.*;
import net.thumbtack.school.buscompany.dto.response.admin.BusDtoResponse;
import net.thumbtack.school.buscompany.dto.response.admin.ScheduleDtoResponse;

import java.util.List;

@Getter
public class GetTripAdminDtoResponse extends GetTripDtoResponse {
    private boolean approved;

    public GetTripAdminDtoResponse(int tripId, @NonNull String fromStation,
                                   @NonNull String toStation, @NonNull String start,
                                   @NonNull String duration, int price,
                                   BusDtoResponse bus, ScheduleDtoResponse schedule,
                                   List<String> dates, boolean approved) {
        super(tripId, fromStation, toStation, start, duration, price, bus, schedule, dates);
        this.approved = approved;
    }
}
