package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Bus {
    private int id;
    @NonNull
    private String busName;
    private int placeCount;
}
