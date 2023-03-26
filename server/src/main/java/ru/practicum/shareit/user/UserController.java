package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUsers() {
        List<UserDto> users = userService.getAllUsers();
        log.info("Server: Get all users={} : {}", users.size(), users);
        return users;
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        UserDto userDto = userService.getUserById(userId);
        log.info("Server: Get user {}, userId={}", userDto, userId);
        return userDto;
    }

    @PostMapping
    public UserDto saveNewUser(@RequestBody UserDto userDto) {
        UserDto newUserDto = userService.saveNewUser(userDto);
        log.info("Server: Save new user {}", newUserDto);
        return newUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody UserDto userDto) {
        UserDto updateUserDto = userService.updateUser(userId, userDto);
        log.info("Server: Update user data {}, userId={}", updateUserDto, userId);
        return updateUserDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        userService.deleteUserById(userId);
        log.info("Server: Delete userId={}", userId);
    }
}
