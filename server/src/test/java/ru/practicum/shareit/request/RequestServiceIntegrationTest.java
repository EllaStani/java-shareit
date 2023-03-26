package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.user.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
class RequestServiceIntegrationTest {
    private final EntityManager em;
    private final RequestService requestService;

    @Test
    void getAllItemRequest() {
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@yandex.ru");

        Item item1 = new Item();
        item1.setName("item1");
        item1.setDescription("itemDescription1");
        item1.setAvailable(true);
        item1.setOwner(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@yandex.ru");

        ItemRequest request1 = new ItemRequest();
        request1.setDescription("request1");
        request1.setCreated(LocalDateTime.parse("2023-03-01T00:09:00"));
        request1.setRequestor(user2);

        ItemRequest request2 = new ItemRequest();
        request2.setDescription("request2");
        request2.setCreated(LocalDateTime.parse("2023-03-02T00:09:00"));
        request2.setRequestor(user1);

        User user3 = new User();
        user3.setName("user3");
        user3.setEmail("user3@yandex.ru");

        Item item2 = new Item();
        item2.setName("item2");
        item2.setDescription("itemDescription2");
        item2.setAvailable(true);
        item2.setOwner(user3);
        item2.setRequest(request2);

        User user4 = new User();
        user4.setName("user4");
        user4.setEmail("user4@yandex.ru");

        Item item3 = new Item();
        item3.setName("item3");
        item3.setDescription("itemDescription3");
        item3.setAvailable(true);
        item3.setOwner(user4);
        item3.setRequest(request2);

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);
        em.persist(user4);
        em.persist(item1);
        em.persist(request1);
        em.persist(request2);
        em.persist(item2);
        em.persist(item3);
        em.flush();

        List<RequestOutDto> targetRequests = requestService.getAllItemRequest(user2.getId(), 0, 10);

        Assertions.assertNotNull(targetRequests);
        Assertions.assertEquals(targetRequests.size(), 1);
        Assertions.assertEquals(targetRequests.get(0).getDescription(), request2.getDescription());
        Assertions.assertEquals(targetRequests.get(0).getItems().size(), 2);
    }
}