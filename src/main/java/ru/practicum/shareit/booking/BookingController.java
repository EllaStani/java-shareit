package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping(value = "/{bookingId}")
    public BookingOutDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long bookingId) {
        BookingOutDto booking = bookingService.getBookingById(userId, bookingId);
        log.info("Get-запрос: Бронь с id={} : - {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public List<BookingOutDto> getBookingBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                @RequestParam(value = "state", defaultValue = "ALL") String state) {
        List<BookingOutDto> bookings = bookingService.getAllBookingByBooker(state, bookerId);
        log.info("Get -запрос:  У пользователя с id = {} всего бронирований {} : - {}", bookerId, bookings.size(), bookings);
        return bookings;
    }

    @GetMapping("/owner")
    public List<BookingOutDto> getAllBookingOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @RequestParam(value = "state", defaultValue = "ALL") String state) {
        List<BookingOutDto> bookings = bookingService.getAllBookingByOwner(state, ownerId);
        log.info("Get -запрос:  У владельца с id {} брони: {}", ownerId, bookings);
        return bookings;
    }

    @PostMapping
    public BookingOutDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                        @RequestBody BookingInDto bookingInDto) {
        BookingOutDto newBooking = bookingService.saveNewBooking(bookerId, bookingInDto);
        log.info("Post-запрос:  пользователь {} забронировал вещь: {}", bookerId, bookingInDto);
        return newBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutDto approvedBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                         @RequestParam String approved,
                                         @PathVariable("bookingId") long bookingId) {
        BookingOutDto booking = bookingService.updateBooking(bookerId, bookingId, approved);
        log.info("Patch-Запрос: бронирование подтверждено владельцем вещи {}", booking);
        return booking;
    }
}
