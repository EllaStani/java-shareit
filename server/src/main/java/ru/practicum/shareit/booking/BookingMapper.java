package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingOutDto mapToBookingOutDto(Booking booking) {
        return new BookingOutDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                new BookingBookerDto(booking.getBooker().getId(), booking.getBooker().getName()),
                new BookingItemDto(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public static List<BookingOutDto> mapToListBookingOutDto(List<Booking> bookings) {
        List<BookingOutDto> bookingDtos = bookings.stream()
                .map(BookingMapper::mapToBookingOutDto)
                .collect(Collectors.toList());
        return bookingDtos;
    }

    public static Booking mapToBooking(Item item, User booker, BookingInDto bookingInDto) {
        Booking booking = new Booking();
        booking.setStart(bookingInDto.getStart());
        booking.setEnd(bookingInDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingLastDto mapToBookingLastDto(Booking booking) {
        return new BookingLastDto(booking.getId(), booking.getBooker().getId());
    }

    public static BookingNextDto mapToBookingNextDto(Booking booking) {
        return new BookingNextDto(booking.getId(), booking.getBooker().getId());
    }
}
