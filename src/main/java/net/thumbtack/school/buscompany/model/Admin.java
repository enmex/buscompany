package net.thumbtack.school.buscompany.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends User{
    @Getter
    @Setter
    @NonNull
    private String position;
}
