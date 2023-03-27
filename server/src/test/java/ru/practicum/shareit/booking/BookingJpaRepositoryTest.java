package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.common.FromSizeRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.booking.BookingStatus.*;

@DataJpaTest
public class BookingJpaRepositoryTest {
    @Autowired
    private BookingJpaRepository bookingRepository;
    @Autowired
    private ItemJpaRepository itemRepository;
    @Autowired
    private UserJpaRepository userRepository;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private Booking booking3;

    @BeforeEach
    public void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        item1 = itemRepository.save(
                new Item(1L, "item1", "itemDescription1", true, user1, null));
        user2 = userRepository.save(new User(2L, "user2", "user2@yandex.ru"));
        item2 = itemRepository.save(
                new Item(2L, "item2", "itemDescription2", true, user2, null));
        user3 = userRepository.save(new User(3L, "user3", "user3@yandex.ru"));
        booking1 = bookingRepository.save(new Booking(1L, LocalDateTime.parse("2023-03-01T00:09:00"),
                LocalDateTime.parse("2023-03-03T00:09:00"), item1, user2, CANCELED));
        booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.parse("2023-03-10T00:09:00"),
                LocalDateTime.parse("2023-03-28T00:09:00"), item1, user3, APPROVED));
        booking3 = bookingRepository.save(new Booking(3L, LocalDateTime.parse("2023-03-05T00:09:00"),
                LocalDateTime.parse("2023-03-15T00:09:00"), item2, user3, REJECTED));
    }

    @Test
    public void findBookingByBooker_Id() {
        Sort startSort = Sort.by("start").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, startSort);

        var result = bookingRepository.findBookingByBooker_Id(user3.getId(), pageable);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking2, result.get(0));
        assertEquals(booking3, result.get(1));
    }

    @Test
    public void findByBooker_IdAndStartIsAfter() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findByBooker_IdAndStartIsAfter(
                user3.getId(), LocalDateTime.parse("2023-03-04T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking3, result.get(0));
        assertEquals(booking2, result.get(1));
    }

    @Test
    public void findByBooker_IdAndEndIsBefore() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findByBooker_IdAndEndIsBefore(
                user3.getId(), LocalDateTime.parse("2023-03-31T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking3, result.get(0));
        assertEquals(booking2, result.get(1));
    }

    @Test
    public void findByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                user3.getId(), LocalDateTime.parse("2023-03-20T00:09:00"),
                LocalDateTime.parse("2023-03-20T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking2, result.get(0));
    }

    @Test
    public void findBookingByBooker_IdAndStatus() {
        var result = bookingRepository.findBookingByBooker_IdAndStatus(
                user3.getId(), REJECTED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking3, result.get(0));
    }

    @Test
    public void findBookingByItemOwnerId() {
        Sort startSort = Sort.by("start").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, startSort);

        var result = bookingRepository.findBookingByItemOwnerId(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking2, result.get(0));
        assertEquals(booking1, result.get(1));
    }

    @Test
    public void findBookingByItemOwnerIdAndStatus() {
        var result = bookingRepository.findBookingByItemOwnerIdAndStatus(
                user1.getId(), CANCELED);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void findBookingByItemOwnerIdAndStartIsAfter() {
        Sort startSort = Sort.by("start").descending();

        var result = bookingRepository.findBookingByItemOwnerIdAndStartIsAfter(
                user1.getId(), LocalDateTime.parse("2023-02-01T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(booking2, result.get(0));
        assertEquals(booking1, result.get(1));
    }

    @Test
    public void findBookingByItemOwnerIdAndEndIsBefore() {
        Sort startSort = Sort.by("start").descending();

        var result = bookingRepository.findBookingByItemOwnerIdAndEndIsBefore(
                user1.getId(), LocalDateTime.parse("2023-03-10T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                user1.getId(), LocalDateTime.parse("2023-03-02T00:09:00"),
                LocalDateTime.parse("2023-03-02T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void findBookingByItemIdAndEndIsBefore() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findBookingByItemIdAndEndIsBefore(
                item1.getId(), LocalDateTime.parse("2023-03-05T00:09:00"), startSort);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking1, result.get(0));
    }

    @Test
    public void findBookingByItemIdAndStartIsAfter() {
        Sort startSort = Sort.by("start");

        var result = bookingRepository.findBookingByItemIdAndStartIsAfterAndStatusIsNot(
                item1.getId(), LocalDateTime.parse("2023-03-05T00:09:00"), REJECTED, startSort);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking2, result.get(0));
    }

    @Test
    public void findByBooker_IdAndItem_IdAndEndIsBefore() {

        var result = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(user3.getId(),
                item1.getId(), LocalDateTime.parse("2023-03-31T00:09:00"));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking2, result.get(0));
    }
}