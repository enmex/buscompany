package net.thumbtack.school.buscompany.dto.response.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.thumbtack.school.buscompany.service.GlobalErrorHandler;

import java.util.List;

@AllArgsConstructor
@Getter
public class ErrorDtoResponse {
    private List<GlobalErrorHandler.Error> errors;
}
