package net.thumbtack.school.buscompany.dto.response.common.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.thumbtack.school.buscompany.dto.response.client.PassengerDtoResponse;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class GetOrderDtoResponse {
    private int orderId;
    private int tripId;
    private String fromStation;
    private String toStation;
    private String busName;
    private String date;
    private String start;
    private String duration;
    private int price;
    private int totalPrice;
    private List<PassengerDtoResponse> passengers;
}
