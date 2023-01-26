package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingLastDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    private Long ownerId;
    private List<CommentDto> comments;
    private BookingLastDto lastBooking;
    private BookingNextDto nextBooking;
}
