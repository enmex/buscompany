package net.thumbtack.school.buscompany.model;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public abstract class User {
    protected String id;

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
}
