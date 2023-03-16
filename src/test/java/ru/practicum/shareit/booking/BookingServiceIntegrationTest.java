package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;

    @Test
    public void getBookingById() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("itemDescription1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.parse("2023-05-01T00:09:00"));
        booking1.setEnd(LocalDateTime.parse("2023-05-03T00:09:00"));
        booking1.setStatus(WAITING);
        booking1.setBooker(user2);
        booking1.setItem(item1);

        em.persist(user1);
        em.persist(user2);
        em.persist(item1);
        em.persist(booking1);
        em.flush();

        BookingOutDto targetBooking = bookingService.getBookingById(user2.getId(), booking1.getId());

        Assertions.assertNotNull(targetBooking.getId());
        Assertions.assertEquals(targetBooking.getStart(), booking1.getStart());
        Assertions.assertEquals(targetBooking.getEnd(), booking1.getEnd());
        Assertions.assertEquals(targetBooking.getStatus(), booking1.getStatus());
        Assertions.assertEquals(targetBooking.getBooker().getName(), user2.getName());
        Assertions.assertEquals(booking1.getItem().getName(), item1.getName());
    }

    @Test
    public void saveNewBooking() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("itemDescription1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        em.persist(user1);
        em.persist(user2);
        em.persist(item1);

        BookingInDto bookingInDto = new BookingInDto();
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(LocalDateTime.parse("2023-05-01T00:09:00"));
        bookingInDto.setEnd(LocalDateTime.parse("2023-05-03T00:09:00"));

        BookingOutDto bookingOutDto = bookingService.saveNewBooking(user2.getId(), bookingInDto);

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingOutDto.getId()).getSingleResult();

        Assertions.assertNotNull(booking.getId());
        Assertions.assertEquals(booking.getStart(), bookingInDto.getStart());
        Assertions.assertEquals(booking.getEnd(), bookingInDto.getEnd());
        Assertions.assertEquals(booking.getStatus(), bookingOutDto.getStatus());
        Assertions.assertEquals(booking.getBooker().getId(), user2.getId());
        Assertions.assertEquals(booking.getItem().getId(), item1.getId());
    }
}