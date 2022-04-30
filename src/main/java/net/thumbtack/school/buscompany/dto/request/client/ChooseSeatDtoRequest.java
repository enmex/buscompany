package net.thumbtack.school.buscompany.dto.request.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChooseSeatDtoRequest {
    private int orderId;
    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    private String passport;
    @NotNull
    private int place;
}
