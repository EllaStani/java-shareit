package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    List<User> getAllUsers();

    User getUserById(long userId);

    User saveNewUser(User user);

    User updateUser(long userId, User user);

    void deleteUserById(long userId);
}
