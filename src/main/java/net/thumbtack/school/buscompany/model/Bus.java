package net.thumbtack.school.buscompany.model;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bus {
    private String id;
    @NonNull
    private String busName;
    private String placeCount;
}
