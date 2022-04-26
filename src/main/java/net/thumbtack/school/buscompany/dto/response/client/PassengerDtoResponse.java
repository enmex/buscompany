package net.thumbtack.school.buscompany.dto.response.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassengerDtoResponse {
    private String firstName;
    private String lastName;
    private String passport;
}
