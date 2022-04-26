package net.thumbtack.school.buscompany.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChooseSeatDtoResponse {
    private int orderId;
    private String ticket;
    private String lastName;
    private String firstName;
    private String passport;
    private String place;
}
