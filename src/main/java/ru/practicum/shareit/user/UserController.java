package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.info("Get-запрос: всего пользователей={} : {}", users.size(), users);
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Get-запрос: id={} - пользователь: {}", userId, userDto);
        return userDto;
    }

    @PostMapping
    public UserDto saveNewUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        UserDto newUserDto = userService.saveNewUser(userDto);
        log.info("Post-запрос: добавлен новый пользователь: {}", newUserDto);
        return newUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @Validated({Update.class}) @RequestBody UserDto userDto) {
        UserDto updateUserDto = userService.updateUser(userId, userDto);
        log.info("Patch-запрос: обновлены данные пользователя: {}", updateUserDto);
        return updateUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        log.info("Delete-запрос:  пользователь с id={} удален из системы", userId);
    }
}
