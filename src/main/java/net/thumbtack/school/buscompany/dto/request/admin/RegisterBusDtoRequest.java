package net.thumbtack.school.buscompany.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterBusDtoRequest {
    @NotNull
    private String busName;
    @NotNull
    private String placeCount;
}
