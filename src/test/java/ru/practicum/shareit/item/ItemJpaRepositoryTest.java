package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.validation.FromSizeRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemJpaRepositoryTest {
    @Autowired
    ItemJpaRepository itemRepository;
    @Autowired
    private UserJpaRepository userRepository;
    private User user1;
    private Item item1;
    private User user2;
    private Item item2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        item1 = itemRepository.save(
                new Item(1L, "item1", "itemDescription1", true, user1, null));
        user2 = userRepository.save(new User(2L, "user2", "user2@yandex.ru"));
        item2 = itemRepository.save(
                new Item(2L, "item2", "itemDescription2", true, user2, null));
    }

    @Test
    void findItemByOwnerId() {
        Sort idSort = Sort.by("id");
        Pageable pageable = FromSizeRequest.of(0, 10, idSort);

        var result = itemRepository.findItemByOwnerId(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    void findItemByRequestId() {
        var result = itemRepository.findItemByRequestId(user2.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void search() {
        Sort idSort = Sort.by("id").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, idSort);

        var result = itemRepository.search("Description", pageable);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item2, result.get(0));
        assertEquals(item1, result.get(1));
    }
}