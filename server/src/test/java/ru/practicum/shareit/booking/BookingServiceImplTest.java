package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.common.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.*;

public class BookingServiceImplTest {
    private BookingService bookingService;
    private BookingJpaRepository bookingRepository;
    private UserJpaRepository userRepository;
    private ItemJpaRepository itemRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;
    private Booking booking4;
    private Pageable pageable;
    private Sort startSort;

    @BeforeEach
    public void setUp() {
        bookingRepository = mock(BookingJpaRepository.class);
        itemRepository = mock(ItemJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);

        startSort = Sort.by("start").descending();
        pageable = FromSizeRequest.of(0, 10, startSort);

        user1 = new User(1L, "user1", "user1@mail.ru");
        user2 = new User(2L, "user2", "user2@yandex.ru");
        user3 = new User(3L, "user3", "user3@yandex.ru");

        item1 = new Item(1L, "item1", "itemDescription1", true, user1, null);
        item2 = new Item(2L, "item2", "itemDescription2", false, user2, null);

        booking1 = new Booking(1L, LocalDateTime.parse("2023-03-01T00:09:00"),
                LocalDateTime.parse("2023-03-03T00:09:00"), item1, user2, APPROVED);
        booking2 = new Booking(2L, LocalDateTime.parse("2023-03-08T00:09:00"),
                LocalDateTime.parse("2023-03-28T00:09:00"), item1, user3, CANCELED);
        booking3 = new Booking(3L, LocalDateTime.parse("2023-03-05T00:09:00"),
                LocalDateTime.parse("2023-03-15T00:09:00"), item2, user3, REJECTED);
        booking4 = new Booking(4L, LocalDateTime.parse("2023-05-01T00:09:00"),
                LocalDateTime.parse("2023-05-03T00:09:00"), item1, user2, WAITING);
    }

    @Test
    public void getAllBookingByBookerForStateALL() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findBookingByBooker_Id(user2.getId(), pageable))
                .thenReturn(List.of(booking4, booking1));

        var result = bookingService.getAllBookingByBooker("ALL", user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(booking4.getId(), result.get(0).getId());
        Assertions.assertEquals(booking1.getId(), result.get(1).getId());
    }

    @Test
    public void getAllBookingByBookerForStateWAITING() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findBookingByBooker_IdAndStatus(user2.getId(), WAITING))
                .thenReturn(List.of(booking4));

        var result = bookingService.getAllBookingByBooker("WAITING", user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByBookerForStateREJECTED() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(bookingRepository.findBookingByBooker_IdAndStatus(user3.getId(), REJECTED))
                .thenReturn(List.of(booking3));

        var result = bookingService.getAllBookingByBooker("REJECTED", user3.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking3.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByBookerForStateFUTURE() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBooker_IdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking4));

        var result = bookingService.getAllBookingByBooker("FUTURE", user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByBookerForStateCURRENT() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking2, booking3));

        var result = bookingService.getAllBookingByBooker("CURRENT", user3.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(booking2.getId(), result.get(0).getId());
        Assertions.assertEquals(booking3.getId(), result.get(1).getId());
    }

    @Test
    public void getAllBookingByBookerForStatePAST() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findByBooker_IdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking1));

        var result = bookingService.getAllBookingByBooker("PAST", user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking1.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByBookerUnknownUser() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getAllBookingByBooker("PAST", 100L, 0, 10);
                });
    }

    @Test
    public void getAllBookingByOwnerForStateALL() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findBookingByItemOwnerId(user1.getId(), pageable))
                .thenReturn(List.of(booking2, booking1));

        var result = bookingService.getAllBookingByOwner("ALL", user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(booking2.getId(), result.get(0).getId());
        Assertions.assertEquals(booking1.getId(), result.get(1).getId());
    }

    @Test
    public void getAllBookingByOwnerForStateWAITING() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(user1.getId(), WAITING))
                .thenReturn(List.of(booking4));

        var result = bookingService.getAllBookingByOwner("WAITING", user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByOwnerForStateREJECTED() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(user2.getId(), REJECTED))
                .thenReturn(List.of(booking3));

        var result = bookingService.getAllBookingByOwner("REJECTED", user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking3.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByOwnerForStateFUTURE() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking4));

        var result = bookingService.getAllBookingByOwner("FUTURE", user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByOwnerForStateCURRENT() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking2));

        var result = bookingService.getAllBookingByOwner("CURRENT", user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking2.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByOwnerForStatePAST() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking1));

        var result = bookingService.getAllBookingByOwner("PAST", user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(booking1.getId(), result.get(0).getId());
    }

    @Test
    public void getAllBookingByOwnerUnknownUser() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getAllBookingByOwner("PAST", 100L, 0, 10);
                });
    }

    @Test
    public void getBookingByIdForOwner() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));

        var result = bookingService.getBookingById(user1.getId(), booking1.getId());
        System.out.println("result = " + result);

        Assertions.assertEquals(booking1.getId(), result.getId());
        Assertions.assertEquals(booking1.getStart(), result.getStart());
        Assertions.assertEquals(booking1.getEnd(), result.getEnd());
        Assertions.assertEquals(booking1.getStatus(), result.getStatus());
        Assertions.assertEquals(user2.getId(), result.getBooker().getId());
        Assertions.assertEquals(item1.getId(), result.getItem().getId());
    }

    @Test
    public void getBookingByIdForBooker() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));

        var result = bookingService.getBookingById(user2.getId(), booking1.getId());
        System.out.println("result = " + result);

        Assertions.assertEquals(booking1.getId(), result.getId());
        Assertions.assertEquals(booking1.getStart(), result.getStart());
        Assertions.assertEquals(booking1.getEnd(), result.getEnd());
        Assertions.assertEquals(booking1.getStatus(), result.getStatus());
        Assertions.assertEquals(user2.getId(), result.getBooker().getId());
        Assertions.assertEquals(item1.getId(), result.getItem().getId());
    }

    @Test
    public void getBookingByIdUnknownUser() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getBookingById(100L, booking1.getId());
                });
    }

    @Test
    public void getUnknownBookingById() {
        Booking nullBooking = new Booking();
        nullBooking = null;
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nullBooking));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getBookingById(user1.getId(), 99L);
                });
    }

    @Test
    public void getBookingByIdErrorUser() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(bookingRepository.findById(booking1.getId())).thenReturn(Optional.of(booking1));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.getBookingById(user3.getId(), booking1.getId());
                });
    }

    @Test
    public void saveNewBooking() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5));

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.save(any())).thenReturn(booking1);

        var result = bookingService.saveNewBooking(user2.getId(), bookingInDto);

        Assertions.assertEquals(booking1.getId(), result.getId());
        Assertions.assertEquals(booking1.getStart(), result.getStart());
        Assertions.assertEquals(booking1.getEnd(), result.getEnd());
        Assertions.assertEquals(booking1.getStatus(), result.getStatus());
        Assertions.assertEquals(user2.getId(), result.getBooker().getId());
        Assertions.assertEquals(item1.getId(), result.getItem().getId());
    }

    @Test
    public void saveNewBookingUnknownUserId() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5));
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.saveNewBooking(100L, bookingInDto);
                });
    }

    @Test
    public void saveNewBookingUnknownItem() {
        BookingInDto bookingInDto = new BookingInDto(99L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5));
        Item nullItem = new Item();
        nullItem = null;

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(99L)).thenReturn(Optional.ofNullable(nullItem));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.saveNewBooking(user2.getId(), bookingInDto);
                });
    }

    @Test
    public void saveNewBookingFromOwner() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5));

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.saveNewBooking(user1.getId(), bookingInDto);
                });
    }

    @Test
    public void saveNewBookingAvailableItemIsFalse() {
        BookingInDto bookingInDto = new BookingInDto(2L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(5));

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.findById(item2.getId())).thenReturn(Optional.of(item2));

        Assertions.assertThrows(ValidationException.class,
                () -> {
                    bookingService.saveNewBooking(user1.getId(), bookingInDto);
                });
    }

    @Test
    public void saveNewBookingStartAfterEnd() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().plusDays(7),
                LocalDateTime.now().plusDays(5));

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        Assertions.assertThrows(ValidationException.class,
                () -> {
                    bookingService.saveNewBooking(user2.getId(), bookingInDto);
                });
    }

    @Test
    public void saveNewBookingStartBeforeNow() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(5));

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        Assertions.assertThrows(ValidationException.class,
                () -> {
                    bookingService.saveNewBooking(user2.getId(), bookingInDto);
                });
    }

    @Test
    public void saveNewBookingEndBeforeNow() {
        BookingInDto bookingInDto = new BookingInDto(1L, LocalDateTime.now().plusDays(3),
                LocalDateTime.now().minusDays(1));

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        Assertions.assertThrows(ValidationException.class,
                () -> {
                    bookingService.saveNewBooking(user2.getId(), bookingInDto);
                });
    }

    @Test
    public void updateBooking() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        var result = bookingService.updateBooking(user1.getId(), item1.getId(), "false");

        Assertions.assertEquals(booking1.getId(), result.getId());
        Assertions.assertEquals(REJECTED, result.getStatus());
    }

    @Test
    public void updateBookingUnknownOwner() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.updateBooking(100L, item1.getId(), "false");
                });
    }

    @Test
    public void updateUnknownBooking() {
        Booking nullBooking = new Booking();
        nullBooking = null;
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(nullBooking));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.updateBooking(user1.getId(), 99L, "false");
                });
    }

    @Test
    public void updateBookingErrorOwner() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    bookingService.updateBooking(user2.getId(), item1.getId(), "false");
                });
    }

    @Test
    public void updateBookingRepeatApproved() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking1));

        Assertions.assertThrows(ValidationException.class,
                () -> {
                    bookingService.updateBooking(user1.getId(), item1.getId(), "true");
                });
    }
}