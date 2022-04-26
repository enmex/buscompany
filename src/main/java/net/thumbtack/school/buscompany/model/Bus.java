package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Bus {
    private int id;
    @NonNull
    private String busName;
    private String placeCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bus)) return false;
        Bus bus = (Bus) o;
        return getId() == bus.getId() && getBusName().equals(bus.getBusName()) && Objects.equals(getPlaceCount(), bus.getPlaceCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBusName(), getPlaceCount());
    }
}
