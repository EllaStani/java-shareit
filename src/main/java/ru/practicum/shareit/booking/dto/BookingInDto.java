package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingInDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
