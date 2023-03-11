package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1L, "testUser", "test@email.ru");
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value(userDto.getEmail()));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    void getUnknownUserById() throws Exception {
        when(userService.getUserById(100L)).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(get("/users/{userId}", 100L))
                .andExpect(status().is(404));
    }

    @Test
    void saveNewUser() throws Exception {
        UserDto saveUserDto = new UserDto(null, "testUser", "test@email.ru");
        when(userService.saveNewUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(saveUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(userDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(userDto.getEmail()));
    }

    @Test
    @DisplayName("Если нет name, то возвращается код 400")
    void saveNewUserNoName() throws Exception {
        UserDto failUserDto = new UserDto(null, null, "test@email.ru");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(failUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Если нет email, то возвращается код 400")
    void saveNewUserNoEmail() throws Exception {
        UserDto failUserDto = new UserDto(null, "failUser", null);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(failUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Если email некорректный, то возвращается код 400")
    void saveNewUserFailEmail() throws Exception {
        UserDto failUserDto = new UserDto(null, "failUser", "user.com");

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(failUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateUser() throws Exception {
        UserDto updateUserDto = new UserDto(1L, "updateUser", "test@email.ru");

        when(userService.updateUser(anyLong(), any()))
                .thenReturn(updateUserDto);

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(updateUserDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(updateUserDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(updateUserDto.getEmail()));
    }

    @Test
    void updateUnknownUser() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(patch("/users/{userId}", 100L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).deleteUserById(1L);
        mockMvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(1L);
    }
}