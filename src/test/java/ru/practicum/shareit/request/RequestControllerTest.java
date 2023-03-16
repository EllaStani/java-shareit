package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
@AutoConfigureMockMvc
public class RequestControllerTest {
    @MockBean
    private RequestService requestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private RequestOutDto requestOutDto;

    @BeforeEach
    void setUp() {
        requestOutDto = new RequestOutDto();
        requestOutDto.setId(1L);
        requestOutDto.setCreated(LocalDateTime.parse("2023-03-01T00:09:00"));
        requestOutDto.setDescription("request");
    }

    @Test
    public void getRequests() throws Exception {
        when(requestService.getItemRequestByRequestorId(1L))
                .thenReturn(List.of(requestOutDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created").value("2023-03-01T00:09:00"));
        verify(requestService, times(1))
                .getItemRequestByRequestorId(1L);
    }

    @Test
    public void getRequestsWithEmptyList() throws Exception {
        when(requestService.getItemRequestByRequestorId(1L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
        verify(requestService, times(1))
                .getItemRequestByRequestorId(1L);
    }

    @Test
    public void getRequestsWithUnknownRequestorId() throws Exception {
        when(requestService.getItemRequestByRequestorId(100L)).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().is(404));
        verify(requestService, times(1))
                .getItemRequestByRequestorId(100L);
    }

    @Test
    public void getItemRequestById() throws Exception {
        when(requestService.getItemRequestById(1L, 1L))
                .thenReturn(requestOutDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value("2023-03-01T00:09:00"));
        verify(requestService, times(1))
                .getItemRequestById(1L, 1L);
    }

    @Test
    public void getUnknownItemRequestById() throws Exception {
        when(requestService.getItemRequestById(1L, 100L)).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/requests/{requestId}", 100L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(404));
        verify(requestService, times(1))
                .getItemRequestById(1L, 100L);
    }

    @Test
    public void getRequestsAllWithEmptyList() throws Exception {
        when(requestService.getAllItemRequest(1L, 0, 10))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
        verify(requestService, times(1))
                .getAllItemRequest(1L, 0, 10);
    }

    @Test
    public void getRequestsAll() throws Exception {
        when(requestService.getAllItemRequest(1L, 0, 10))
                .thenReturn(List.of(requestOutDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].created").value("2023-03-01T00:09:00"));
        verify(requestService, times(1))
                .getAllItemRequest(1L, 0, 10);
    }

    @Test
    public void saveNewItemRequest() throws Exception {
        RequestInDto requestInDto = new RequestInDto("request");
        RequestSaveDto requestSaveDto = new RequestSaveDto(1L, LocalDateTime.parse("2023-03-01T00:09:00"),
                "request");

        when(requestService.saveNewRequest(1L, requestInDto)).thenReturn(requestSaveDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created").value("2023-03-01T00:09:00"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("request"));
        verify(requestService, times(1))
                .saveNewRequest(1L, requestInDto);
    }

    @Test
    public void saveNewItemRequestBlankName() throws Exception {
        RequestInDto failRequestInDto = new RequestInDto("");

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(failRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void saveNewItemRequestNoName() throws Exception {
        RequestInDto failRequestInDto = new RequestInDto();

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(failRequestInDto))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void saveNewItemRequestUnknownUserById() throws Exception {
        RequestInDto requestInDto = new RequestInDto("request");
        when(requestService.saveNewRequest(100L, requestInDto)).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestInDto))
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
        verify(requestService, times(1))
                .saveNewRequest(100L, requestInDto);
    }
}