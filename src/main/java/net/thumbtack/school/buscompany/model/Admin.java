package net.thumbtack.school.buscompany.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User{
    @Getter
    @Setter
    @NonNull
    private String position;

    public Admin(String firstName, String lastName, String patronymic, String login, String password) {
        super("0", firstName, lastName, patronymic, login, password);
    }
}
