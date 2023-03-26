package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemInDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;

    @Test
    public void getItemsByUserId() {
        User user = new User();
        user.setName("user1");
        user.setEmail("user1@yandex.ru");
        em.persist(user);

        ItemInDto itemDto = new ItemInDto();
        itemDto.setName("item1");
        itemDto.setDescription("itemDescription1");
        itemDto.setAvailable(true);

        itemDto = itemService.saveNewItem(user.getId(), itemDto);

        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item item = query.setParameter("name", itemDto.getName()).getSingleResult();

        Assertions.assertNotNull(item.getId());
        Assertions.assertEquals(item.getName(), itemDto.getName());
        Assertions.assertEquals(item.getDescription(), itemDto.getDescription());
        Assertions.assertEquals(item.getAvailable(), itemDto.getAvailable());
        Assertions.assertEquals(item.getOwner().getEmail(), user.getEmail());
    }
}