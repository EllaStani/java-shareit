package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBookingByBooker_Id(long userId, Pageable pageable);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime nowDate, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime nowDate, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime nowStartDate, LocalDateTime nowEndDate, Sort sort);

    List<Booking> findBookingByBooker_IdAndStatus(long bookerId, BookingStatus status);

    List<Booking> findBookingByItemOwnerId(long userId, Pageable pageable);

    List<Booking> findBookingByItemOwnerIdAndStatus(long userId, BookingStatus status);

    List<Booking> findBookingByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime nowDate, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime nowDate, Sort sort);

    List<Booking> findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime nowStartDate, LocalDateTime nowEndDate, Sort sort);

    List<Booking> findBookingByItemIdAndEndIsBefore(long itemId, LocalDateTime nowEndDate, Sort sort);

    List<Booking> findBookingByItemIdAndStartIsAfter(long itemId, LocalDateTime nowStartDate, Sort sort);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBefore(long bookerId, long itemId, LocalDateTime nowEndDate);
}
