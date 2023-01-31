package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingLastDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private Long owner;
    private List<CommentDto> comments;
    private BookingLastDto lastBooking;
    private BookingNextDto nextBooking;
}
