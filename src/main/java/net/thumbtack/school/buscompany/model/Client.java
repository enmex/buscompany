package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.List;

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

    @NonNull
    private List<Order> orders;

    public Client(String firstName, String lastName, String patronymic, String login, String password, List<Order> orders) {
        super(0, firstName, lastName, patronymic, login, password, UserType.CLIENT);
        this.orders = orders;
    }
}
