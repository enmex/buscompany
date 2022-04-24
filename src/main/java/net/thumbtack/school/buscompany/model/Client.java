package net.thumbtack.school.buscompany.model;


import lombok.*;

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
}
