package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.id = ?1 AND b.end_date<NOW()" +
            "order by b.end_date DESC ", nativeQuery = true)
    List<Booking> getLastBookingByItemId(long itemId);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.id = ?1 AND b.start_date>NOW()" +
            "order by b.start_date", nativeQuery = true)
    List<Booking> getNextBookingByItemId(long itemId);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 " +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getAllBookingForOwner(long userId);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 " +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getAllBookingForBooker(long userId);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.item_id = ?2 AND b.status<>'REJECTED'", nativeQuery = true)
    List<Booking> getRejectedBookingForBookerByItemId(long bookerId, long itemId);


    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "left join users as u on u.id = b.booker_id " +
            "where i.owner_id = ?1 AND b.start_date>NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getFutureOwnerBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.start_date>NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getFutureBookerBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.status = 'WAITING'" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getWaitingBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 AND b.status = 'WAITING'" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getWaitingOwnerBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.status = 'REJECTED'" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getRejectedBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 AND b.status = 'REJECTED'" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getRejectedOwnerBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.start_date<NOW() AND b.end_date>NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getCurrentBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 AND b.start_date<NOW() AND b.end_date>NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getCurrentOwnerBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where b.booker_id = ?1 AND b.end_date<NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getPastBooking(long booker);

    @Query(value = "select * " +
            "from bookings as b left join items as i on i.id = b.item_id " +
            "where i.owner_id = ?1 AND b.end_date<NOW()" +
            "order by b.start_date DESC ", nativeQuery = true)
    List<Booking> getPastOwnerBooking(long booker);
}
