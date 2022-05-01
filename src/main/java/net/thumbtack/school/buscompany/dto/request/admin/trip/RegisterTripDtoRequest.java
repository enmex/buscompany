package net.thumbtack.school.buscompany.dto.request.admin.trip;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.thumbtack.school.buscompany.validation.Date;

import javax.validation.constraints.NotNull;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterTripDtoRequest {
    @NotNull
    private String busName;
    @NotNull
    private String fromStation;
    @NotNull
    private String toStation;
    @NotNull
    @Date
    private String start;
    @NotNull
    @Date
    private String duration;
    @NotNull
    private int price;

    // REVU а вот тут тоже нужна валидация
    // либо schedule!=null, либо dates!=null, но не оба
    // надо проверить
    // делается кастомными валидаторами на класс в целом
    //https://www.baeldung.com/spring-mvc-custom-validator
    private ScheduleDtoRequest schedule;

    @Date(style = "yyyy-MM-dd")
    private List<String> dates;
}
