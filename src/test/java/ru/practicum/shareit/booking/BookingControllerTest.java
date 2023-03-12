package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingInDto;
import ru.practicum.shareit.booking.dto.BookingOutDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private BookingInDto bookingInDto;
    private BookingOutDto bookingOutDto;

    @BeforeEach
    void setUp() {
        bookingOutDto = new BookingOutDto();
        bookingOutDto.setId(1L);
        bookingOutDto.setStart(LocalDateTime.parse("2023-03-01T00:09:00"));
        bookingOutDto.setEnd(LocalDateTime.parse("2023-03-03T00:09:00"));
        bookingOutDto.setStatus(BookingStatus.WAITING);
    }

    @Test
    void getBookingById() throws Exception {
        when(bookingService.getBookingById(1L, 1L))
                .thenReturn(bookingOutDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("2023-03-03T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getUnknownBookingById() throws Exception {
        when(bookingService.getBookingById(1L, 100L)).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/bookings/{bookingId}", 100L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(404));
    }

    @Test
    void getBookingBooker() throws Exception {
        when(bookingService.getAllBookingByBooker("ALL", 1L, 0, 10))
                .thenReturn(List.of(bookingOutDto));

        mockMvc.perform(get("/bookings?state={state}", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].end").value("2023-03-03T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getBookingUnknownBooker() throws Exception {
        when(bookingService.getAllBookingByBooker("ALL", 100L, 0, 10))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/bookings?state={state}", "ALL")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().is(404));
    }

    @Test
    void getAllBookingOwner() throws Exception {
        when(bookingService.getAllBookingByOwner("ALL", 1L, 0, 10))
                .thenReturn(List.of(bookingOutDto));

        mockMvc.perform(get("/bookings/owner?state={state}", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].start").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].end").value("2023-03-03T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllBookingUnknownOwner() throws Exception {
        when(bookingService.getAllBookingByOwner("ALL", 1L, 0, 10))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/bookings/owner?state={state}", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(404));
    }

    @Test
    void saveNewBooking() throws Exception {
        bookingInDto = new BookingInDto();
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(LocalDateTime.parse("2023-03-01T00:09:00"));
        bookingInDto.setEnd(LocalDateTime.parse("2023-03-03T00:09:00"));

        when(bookingService.saveNewBooking(1L, bookingInDto)).thenReturn(bookingOutDto);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("2023-03-03T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("WAITING"));
    }

    @Test
    void saveNewBookingUnknownUser() throws Exception {
        bookingInDto = new BookingInDto();
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(LocalDateTime.parse("2023-03-01T00:09:00"));
        bookingInDto.setEnd(LocalDateTime.parse("2023-03-03T00:09:00"));

        when(bookingService.saveNewBooking(100L, bookingInDto))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInDto))
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void saveNewBookingErrorValidation() throws Exception {
        bookingInDto = new BookingInDto();
        bookingInDto.setItemId(1L);
        bookingInDto.setStart(LocalDateTime.parse("2023-03-05T00:09:00"));
        bookingInDto.setEnd(LocalDateTime.parse("2023-03-03T00:09:00"));

        when(bookingService.saveNewBooking(1L, bookingInDto))
                .thenThrow(new ValidationException("Error Validation"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(bookingInDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    void approvedBooking() throws Exception {
        bookingOutDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBooking(1L, 1L, "true")).thenReturn(bookingOutDto);

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, "true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.start").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end").value("2023-03-03T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void approvedBookingErrorValidation() throws Exception {
        when(bookingService.updateBooking(1L, 1L, "false"))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, "false")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void approvedBookingUnknownUser() throws Exception {
        bookingOutDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.updateBooking(100L, 1L, "true"))
                .thenThrow(new ValidationException("Error Validation"));

        mockMvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, "true")
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }
}