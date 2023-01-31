package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public List<BookingOutDto> getAllBookingByBooker(String state, long userId) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        checkingExistUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        Sort startSort = Sort.by("start").descending();
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findBookingByBooker_Id(userId, startSort);
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
    public List<BookingOutDto> getAllBookingByOwner(String state, long userId) {
        if (state.equals("UNSUPPORTED_STATUS")) {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        checkingExistUser(userId);
        BookingState bookingState = BookingState.valueOf(state);
        Sort startSort = Sort.by("start").descending();
        List<Booking> bookings = new ArrayList<>();

        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findBookingByItemOwnerId(userId, startSort);
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
        checkingExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();

        if (booking.getBooker().getId() == userId || booking.getItem().getOwnerId() == userId) {
            return BookingMapper.mapToBookingOutDto(booking);
        } else {
            throw new NotFoundException(String.format("Пользователь не владелец или не бронировал данную вещь"));
        }
    }

    @Transactional
    @Override
    public BookingOutDto saveNewBooking(long bookerId, BookingInDto bookingInDto) {
        checkingExistUser(bookerId);
        checkingExistItem(bookingInDto.getItemId());
        Item item = itemRepository.findById(bookingInDto.getItemId()).get();

        if (bookerId == item.getOwnerId()) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }

        User booker = userRepository.findById(bookerId).get();
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
        checkingExistBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getItem().getOwnerId() != bookerId) {
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

    private void checkingExistUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
    }

    private void checkingExistItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Вещь с id=%s не найдена", itemId));
        }
    }

    private void checkingExistBooking(long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException(String.format("Бронирование с id=%s не найдено", bookingId));
        }
    }

    private void validationBooking(Booking booking) {

        if (itemRepository.findById(booking.getItem().getId()).get().getAvailable() == false) {
            throw new ValidationException("Данная вещь не доступна для бронирования");
        }
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
