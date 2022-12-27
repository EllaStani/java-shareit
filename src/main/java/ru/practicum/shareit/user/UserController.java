package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;

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
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        validationCreateUser(userDto);
        UserDto newUserDto = userService.saveNewUser(userDto);
        log.info("Post-запрос: добавлен новый пользователь: {}", newUserDto);
        return newUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        validationUpdateUser(userDto);
        UserDto updateUserDto = userService.updateUser(userId, userDto);
        log.info("Patch-запрос: обновлены данные пользователя: {}", updateUserDto);
        return updateUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        log.info("Delete-запрос:  пользователь с id={} удален из системы", userId);
    }

    private void validationCreateUser(UserDto userDto) {
        if (!StringUtils.hasText(userDto.getName())) {
            log.error("Post-запрос не выполнен: логин либо пустой, либо содержит только пробелы");
            throw new ValidationException("логин не может быть пустым и содержать только пробелы");
        }

        if (!StringUtils.hasLength(userDto.getEmail()) || !userDto.getEmail().contains("@")) {
            log.error("Post-запрос не выполнен: email={} - задан некорректно", userDto.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validationUpdateUser(UserDto userDto) {
        if (StringUtils.hasLength(userDto.getEmail())) {
            if (!userDto.getEmail().contains("@")) {
                log.error("Patch-запрос не выполнен: email={} - задан некорректно", userDto.getEmail());
                throw new ValidationException("электронная почта должна содержать символ @");
            }
        }
    }
}
