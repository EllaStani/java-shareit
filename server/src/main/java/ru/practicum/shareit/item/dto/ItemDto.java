package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingLastDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;
    private BookingLastDto lastBooking;
    private BookingNextDto nextBooking;
}
