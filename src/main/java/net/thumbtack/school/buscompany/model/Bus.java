package net.thumbtack.school.buscompany.model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Bus {
    private int id;
    @NonNull
    private String busName;
    private int placeCount;

    public Bus(int id){
        this.id = id;
    }
}
