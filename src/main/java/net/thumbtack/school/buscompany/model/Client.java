package net.thumbtack.school.buscompany.model;


import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Client extends User{
    @NonNull
    private String email;
    @NonNull
    private String phone;

    public Client(String firstName, String lastName, String patronymic, String login, String password) {
        super(firstName, lastName, patronymic, login, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return getEmail().equals(client.getEmail()) && getPhone().equals(client.getPhone());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEmail(), getPhone());
    }
}
