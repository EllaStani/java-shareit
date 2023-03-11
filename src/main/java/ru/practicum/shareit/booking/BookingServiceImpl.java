package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.validation.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingJpaRepository bookingRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    public List<BookingOutDto> getAllBookingByBooker(String state, long userId, Integer from, Integer size) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        checkingExistUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        Sort startSort = Sort.by("start").descending();
        Pageable pageable = FromSizeRequest.of(from, size, startSort);
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findBookingByBooker_Id(userId, pageable);
                log.info("У пользователя с id = {} всего бронирований {} : - {}",
                        userId, bookings.size(), bookings);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, BookingStatus.WAITING);
                log.info("Данные о бронированиях пользователя с id = {}, ожидающих подтверждения", userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
                log.info("Данные о бронированиях пользователя с id = {}, отклоненных владельцем вещи", userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), startSort);
                log.info("Все предстоящие бронированиях для пользователя с id = {}: - {}", userId, bookings);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), startSort);
                log.info("Данные о текущих бронированиях у пользователя с id = {}", userId);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), startSort);
                log.info("Данные о завершенных бронированиях у пользователя с id = {}", userId);
                break;
            default:
                break;
        }
        return BookingMapper.mapToListBookingOutDto(bookings);
    }

    @Override
    public List<BookingOutDto> getAllBookingByOwner(String state, long userId, Integer from, Integer size) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        checkingExistUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        Sort startSort = Sort.by("start").descending();
        Pageable pageable = FromSizeRequest.of(from, size, startSort);
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findBookingByItemOwnerId(userId, pageable);
                log.info("У владельца с id = {} забронировно {} вещей: - {}",
                        userId, bookings.size(), bookings);
                break;
            case WAITING:
                bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(
                        userId, BookingStatus.WAITING);
                log.info("Все бронирования,ожидающие подтверждения, владельцем с id = {}", userId);
                break;
            case REJECTED:
                bookings = bookingRepository.findBookingByItemOwnerIdAndStatus(
                        userId, BookingStatus.REJECTED);
                log.info("Все бронирования,отклоненные владельцем вещи с id = {}", userId);
                break;
            case FUTURE:
                bookings = bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(
                        userId, LocalDateTime.now(), startSort);
                log.info("Все предстоящие бронированиях для владельца с id = {}: - {}", userId, bookings);
                break;
            case CURRENT:
                bookings = bookingRepository.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, LocalDateTime.now(), LocalDateTime.now(), startSort);
                log.info("Данные о всех текущих бронированиях для владельца с id = {}", userId);
                break;
            case PAST:
                bookings = bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(
                        userId, LocalDateTime.now(), startSort);
                log.info("Данные о завершенных бронированиях у владельца с id = {}", userId);
                break;
            default:
                break;
        }
        return BookingMapper.mapToListBookingOutDto(bookings);
    }

    @Override
    public BookingOutDto getBookingById(long userId, long bookingId) {
        checkingExistUser(userId);
        Booking booking = checkingExistBooking(bookingId);

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return BookingMapper.mapToBookingOutDto(booking);
        } else {
            throw new NotFoundException(String.format("Пользователь не владелец или не бронировал данную вещь"));
        }
    }

    @Transactional
    @Override
    public BookingOutDto saveNewBooking(long bookerId, BookingInDto bookingInDto) {
        User booker = checkingExistUser(bookerId);
        Item item = checkingExistItem(bookingInDto.getItemId());

        if (bookerId == item.getOwner().getId()) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        if (item.getAvailable() == false) {
            throw new ValidationException("Данная вещь не доступна для бронирования");
        }

        Booking booking = BookingMapper.mapToBooking(item, booker, bookingInDto);
        validationBooking(booking);
        booking.setStatus(BookingStatus.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingOutDto(newBooking);
    }

    @Transactional
    @Override
    public BookingOutDto updateBooking(long bookerId, long bookingId, String approved) {
        checkingExistUser(bookerId);
        Booking booking = checkingExistBooking(bookingId);

        if (booking.getItem().getOwner().getId() != bookerId) {
            throw new NotFoundException("Только владелец вещи может подтвердить или отклонить запрос на бронирование!");
        }

        if (approved.equals("true")) {

            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new ValidationException("Бронирование уже подтверждено владельцем");
            } else {
                booking.setStatus(BookingStatus.APPROVED);
                bookingRepository.save(booking);
                return BookingMapper.mapToBookingOutDto(booking);
            }
        } else {
            if (booking.getStatus() == BookingStatus.REJECTED) {
                throw new ValidationException("Бронирование уже отклонено владельцем");
            } else {
                booking.setStatus(BookingStatus.REJECTED);
                bookingRepository.save(booking);
                return BookingMapper.mapToBookingOutDto(booking);
            }
        }
    }

    private User checkingExistUser(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
        return user;
    }

    private Item checkingExistItem(long itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            throw new NotFoundException(String.format("Вещь с id=%s не найдена", itemId));
        }
        return item;
    }

    private Booking checkingExistBooking(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new NotFoundException(String.format("Бронирование с id=%s не найдено", bookingId));
        }
        return booking;
    }

    private void validationBooking(Booking booking) {
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new ValidationException(
                    "Дата начала бронирования не может быть больше даты окончания бронирования");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало бронирования не может быть меньше текущей даты");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Окончание бронирования не может быть меньше текущей даты");
        }
    }
}
