package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestInDto {
    @NotBlank(groups = {Create.class}, message = "Запрос не может быть пустым или содержать только пробелы")
    @NotNull(groups = {Create.class}, message = "Запрос не задан")
    private String description;
}
