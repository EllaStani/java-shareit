package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutDtoTest {
    @Autowired
    private JacksonTester<BookingOutDto> json;

    @Test
    void testBookingOutDto() throws Exception {
        BookingBookerDto booker = new BookingBookerDto(1L, "user1");
        BookingItemDto item = new BookingItemDto(1L, "item1");

        BookingOutDto dto = new BookingOutDto(1L, LocalDateTime.parse("2023-03-01T00:09:00"),
                LocalDateTime.parse("2023-03-03T00:09:00"), BookingStatus.WAITING, booker, item);

        JsonContent<BookingOutDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-03-01T00:09:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-03-03T00:09:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(json.write(dto)).hasJsonPathValue("$.booker", "1", "user1");
        assertThat(json.write(dto)).hasJsonPathValue("$.item", "1", "item1");
    }
}