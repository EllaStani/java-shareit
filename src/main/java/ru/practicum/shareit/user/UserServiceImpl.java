package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers();
        return users == null ? null : UserMapper.mapToListUserDto(users);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.getUserById(userId);
        return user == null ? null : UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto saveNewUser(UserDto userDto) {
        User newUser = userRepository.saveNewUser(UserMapper.mapToUser(userDto));
        return newUser == null ? null : UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        checkingExistUser(userId);
        User updateUser = userRepository.updateUser(userId, UserMapper.mapToUser(userDto));
        return updateUser == null ? null : UserMapper.mapToUserDto(updateUser);
    }

    @Override
    public void deleteUserById(long userId) {
        checkingExistUser(userId);
        userRepository.deleteUserById(userId);
    }

    public User checkingExistUser(long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
        return user;
    }
}
