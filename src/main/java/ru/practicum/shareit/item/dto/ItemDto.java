package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Наименование не может быть пустым или содержать только пробелы")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым или содержать только пробелы")
    private String description;
    @NotNull(message = "Не задан статус доступности для аренды")
    private Boolean available;
    private User owner;
}
