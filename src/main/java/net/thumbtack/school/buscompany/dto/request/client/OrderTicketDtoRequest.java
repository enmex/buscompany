package net.thumbtack.school.buscompany.dto.request.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.school.buscompany.validation.Date;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OrderTicketDtoRequest {
    private int tripId;
    @Date(style = "yyyy-MM-dd")
    private String date;
    @NotNull
    private List<PassengerDtoRequest> passengers;
}
