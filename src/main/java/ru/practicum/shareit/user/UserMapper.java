package ru.practicum.shareit.user;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static List<UserDto> mapToListUserDto(List<User> users) {
        List<UserDto> userDtos = users.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        return userDtos;
    }

    public static User mapToUser(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
