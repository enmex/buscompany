package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Client extends User{
    @NonNull
    private String email;
    @NonNull
    private String phone;
    // REVU а не добавить ли List<Order> ?

    public Client(String firstName, String lastName, String patronymic, String login, String password) {
        super(0, firstName, lastName, patronymic, login, password, "client");
    }
}
