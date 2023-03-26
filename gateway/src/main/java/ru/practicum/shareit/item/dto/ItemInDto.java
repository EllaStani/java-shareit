package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemInDto {
    private Long id;
    @NotBlank(message = "Наименование не может быть пустым или содержать только пробелы")
    private String name;
    @NotBlank(message = "Описание вещи не может быть пустым или содержать только пробелы")
    private String description;
    @NotNull(message = "Не задан статус доступности для аренды")
    private Boolean available;
    private Long requestId;
}
