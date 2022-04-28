package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User{
    @Getter
    @Setter
    @NonNull
    private String position;

    public Admin(String firstName, String lastName, String patronymic, String login, String password) {
        super(0, firstName, lastName, patronymic, login, password, "admin");
    }
}
