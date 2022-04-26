package net.thumbtack.school.buscompany.dto.response.common.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetSettingsDtoResponse {
    private int maxNameLength;
    private int userIdleTimeout;
    private int minPasswordLength;
}
