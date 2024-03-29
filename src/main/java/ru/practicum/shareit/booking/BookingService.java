package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

public interface BookingService {
    BookingOutDto getBookingById(long userId, long bookingId);

    BookingOutDto saveNewBooking(long bookerId, BookingInDto bookingDto);

    BookingOutDto updateBooking(long bookerId, long bookingId, String approved);

    List<BookingOutDto> getAllBookingByBooker(String state, long booker, Integer from, Integer size);

    List<BookingOutDto> getAllBookingByOwner(String state, long booker, Integer from, Integer size);
}
