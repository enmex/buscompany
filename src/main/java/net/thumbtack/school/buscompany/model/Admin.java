package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class Admin extends User{
    @Getter
    @Setter
    @NonNull
    private String position;

    public Admin(String firstName, String lastName, String patronymic, String login, String password) {
        super(0, firstName, lastName, patronymic, login, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        if (!super.equals(o)) return false;
        Admin admin = (Admin) o;
        return getPosition().equals(admin.getPosition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPosition());
    }
}
