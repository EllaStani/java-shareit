package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingInDto {
    private Long itemId;
    @NotNull(groups = {Create.class}, message = "Не задана дата начала бронирования")
    private LocalDateTime start;
    @NotNull(groups = {Create.class}, message = "Не задана дата окончания бронирования")
    private LocalDateTime end;
}
