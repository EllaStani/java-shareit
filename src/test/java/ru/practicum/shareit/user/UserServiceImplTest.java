package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserServiceImpl userService;
    private UserJpaRepository userRepository;
    private User user;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserJpaRepository.class);
        userService = new UserServiceImpl(userRepository);

        user = new User(1L, "testUser", "test@email.ru");
        userDto = new UserDto(1L, "testUser", "test@email.ru");
    }

    @Test
    public void getAllUsers() {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(userDto);

        when(userRepository.findAll()).thenReturn(List.of(user));

        var result = userService.getAllUsers();

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(userDtos, result);
    }

    @Test
    public void getUserById() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.getUserById(userDto.getId());

        assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void getUnknownUserById() {
        User nullUser = new User();
        nullUser = null;
        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));
        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    userService.getUserById(100L);
                });
    }

    @Test
    public void saveNewUser() {
        when(userRepository.save(any())).thenReturn(user);

        var result = userService.saveNewUser(userDto);

        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
    }


    @Test
    public void updateEmailUser() {
        var updateUserDto = new UserDto(null, null, "update@email.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.updateUser(1L, updateUserDto);

        assertNotNull(result);
        Assertions.assertEquals(user.getName(), result.getName());
        Assertions.assertEquals(updateUserDto.getEmail(), result.getEmail());
    }

    @Test
    public void updateNameUser() {
        var updateUserDto = new UserDto(null, "updateUser", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        var result = userService.updateUser(1L, updateUserDto);

        assertNotNull(result);
        Assertions.assertEquals(updateUserDto.getName(), result.getName());
        Assertions.assertEquals(user.getName(), result.getName());
    }

    @Test
    public void updateUnknownUser() {
        var updateUserDto = new UserDto(null, "updateUser", "update@email.ru");
        User nullUser = new User();
        nullUser = null;
        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    userService.updateUser(100L, updateUserDto);
                });
    }

    @Test
    public void deleteUserById() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void deleteUnknownUserById() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class, () -> {
            userService.deleteUserById(anyLong());
        });
    }
}