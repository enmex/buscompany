package net.thumbtack.school.buscompany.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Passenger {
    private int id;
    private String firstName;
    private String lastName;
    private String passport;
}
