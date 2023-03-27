package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.common.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemJpaRepositoryTest {
    @Autowired
    private ItemJpaRepository itemRepository;
    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private RequestJpaRepository requestRepository;
    private User user1;
    private Item item1;
    private User user2;
    private Item item2;
    private ItemRequest request1;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        request1 = requestRepository.save(
                new ItemRequest(1L, "request1", LocalDateTime.parse("2023-03-01T00:09:00"), user1));
        item1 = itemRepository.save(
                new Item(1L, "item1", "itemDescription1", true, user1, request1));

        user2 = userRepository.save(new User(2L, "user2", "user2@yandex.ru"));
        item2 = itemRepository.save(
                new Item(2L, "item2", "itemDescription2", true, user2, null));
    }

    @Test
    public void findItemByOwnerId() {
        Sort idSort = Sort.by("id");
        Pageable pageable = FromSizeRequest.of(0, 10, idSort);

        var result = itemRepository.findItemByOwnerId(user1.getId(), pageable);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    public void findItemByRequestIdWithEmptyList() {
        var result = itemRepository.findItemByRequestId(user2.getId());

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void findItemByRequestId() {
        var result = itemRepository.findItemByRequestId(request1.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    public void findItemByListRequestIds() {
        var result = itemRepository.findItemByListRequestIds(List.of(1L));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    public void search() {
        var result = itemRepository.search("Description");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(item1, result.get(0));
        assertEquals(item2, result.get(1));
    }
}