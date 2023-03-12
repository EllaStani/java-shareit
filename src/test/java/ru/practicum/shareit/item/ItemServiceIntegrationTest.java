package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("user1");
        user.setEmail("user1@yandex.ru");
        em.persist(user);
    }

    @AfterEach
    void afterEach() {
        em.createNativeQuery("truncate table items");
    }

    @Test
    void getItemsByUserId() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("item1");
        itemDto.setDescription("itemDescription1");
        itemDto.setAvailable(true);

        itemDto = itemService.saveNewItem(1L, itemDto);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        Assertions.assertEquals(item.getId(), itemDto.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(item.getOwner(), user);
    }
}