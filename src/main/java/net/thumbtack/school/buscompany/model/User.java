package net.thumbtack.school.buscompany.model;

import lombok.*;

import java.util.Objects;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class User {
    protected int id;

    @NonNull
    protected String firstName;
    @NonNull
    protected String lastName;

    protected String patronymic;
    @NonNull
    protected String login;

    @NonNull
    protected String password;

    public User(@NonNull String firstName, @NonNull String lastName, String patronymic, @NonNull String login, @NonNull String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.patronymic = patronymic;
        this.login = login;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() == user.getId() && getFirstName().equals(user.getFirstName())
                && getLastName().equals(user.getLastName()) && Objects.equals(getPatronymic(), user.getPatronymic())
                && getLogin().equals(user.getLogin()) && getPassword().equals(user.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLastName(), getPatronymic(), getLogin(), getPassword());
    }
}
