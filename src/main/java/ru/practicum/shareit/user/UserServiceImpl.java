package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserJpaRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserMapper.mapToListUserDto(users);
    }

    @Override
    public UserDto getUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        return UserMapper.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto saveNewUser(UserDto userDto) {
        User newUser = userRepository.save(UserMapper.mapToUser(userDto));
        return newUser == null ? null : UserMapper.mapToUserDto(newUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User updateUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));

        if (userDto.getName() != null) {
            updateUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            updateUser.setEmail(userDto.getEmail());
        }

        userRepository.save(updateUser);
        return UserMapper.mapToUserDto(updateUser);
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
        userRepository.deleteById(userId);
    }
}
