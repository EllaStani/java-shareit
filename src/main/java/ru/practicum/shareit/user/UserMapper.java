package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getEmail(), user.getName());
    }

    public static List<UserDto> mapToListUserDto(Iterable<User> users) {
        List<UserDto> userDtos = new ArrayList<>();

        for (User user : users) {
            userDtos.add(mapToUserDto(user));
        }

        return userDtos;
    }

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }
}
