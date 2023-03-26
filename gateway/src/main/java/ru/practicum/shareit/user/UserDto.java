package ru.practicum.shareit.user;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.common.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    @NotBlank(groups = {Create.class}, message = "Логин не может быть пустым или содержать только пробелы")
    private String name;

    @Email(groups = {Create.class, Update.class}, message = "email не соответствует формату электронной почты")
    @NotNull(groups = {Create.class}, message = "email не задан")
    private String email;
}
