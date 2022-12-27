package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class InMemoryUserRepository implements UserRepository {
    private long id = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User saveNewUser(User user) {
        checkingForUniqEmail(user.getEmail());
        emails.add(user.getEmail());
        long newId = generateId();
        user.setId(newId);
        users.put(newId, user);
        return user;
    }

    @Override
    public User updateUser(long userId, User user) {
        User updateUser = getUserById(userId);

        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            if (user.getEmail() != updateUser.getEmail()) {
                checkingForUniqEmail(user.getEmail());
                emails.remove(updateUser.getEmail());
                emails.add(user.getEmail());
                updateUser.setEmail(user.getEmail());
            }
        }
        return updateUser;
    }

    @Override
    public void deleteUserById(long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    private Long generateId() {
        return ++this.id;
    }

    private void checkingForUniqEmail(String email) {
        if (!emails.add(email)) {
            throw new ConflictException(String.format("Пользователь с email=%s уже существует", email));
        }
    }
}
