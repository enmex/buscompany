package net.thumbtack.school.buscompany.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderTicketDtoResponse {
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
