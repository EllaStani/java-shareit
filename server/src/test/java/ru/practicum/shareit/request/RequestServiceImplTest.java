package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.common.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RequestServiceImplTest {
    private RequestService requestService;
    private RequestJpaRepository requestRepository;
    private ItemJpaRepository itemRepository;
    private UserJpaRepository userRepository;
    private ItemRequest request1;
    private ItemRequest request2;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;

    @BeforeEach
    public void setUp() {
        requestRepository = mock(RequestJpaRepository.class);
        itemRepository = mock(ItemJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        requestService = new RequestServiceImpl(requestRepository, userRepository, itemRepository);

        user1 = new User(1L, "user1", "user1@yandex.ru");
        user2 = new User(2L, "user2", "user2@yandex.ru");
        user3 = new User(3L, "user3", "user3@yandex.ru");

        request1 = new ItemRequest(1L, "request1", LocalDateTime.parse("2023-03-01T00:09:00"), user1);
        request2 = new ItemRequest(2L, "request2", LocalDateTime.parse("2023-03-05T00:09:00"), user2);

        item1 = new Item(1L, "item1", "itemDescription1", true, user1, request1);
        item2 = new Item(2L, "item2", "itemDescription2", true, user2, null);
    }

    @Test
    public void getAllItemRequest() {
        Sort createdSort = Sort.by("created").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, createdSort);

        when(requestRepository.findItemRequestByRequestorIdIsNot(user2.getId(), pageable)).thenReturn(List.of(request1));
        when(itemRepository.findItemByListRequestIds(List.of(request1.getId()))).thenReturn(List.of(item1));

        var result = requestService.getAllItemRequest(user2.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(request1.getId(), result.get(0).getId());
        Assertions.assertEquals(request1.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(item1.getId(), result.get(0).getItems().get(0).getId());
        Assertions.assertEquals(item1.getName(), result.get(0).getItems().get(0).getName());
    }

    @Test
    public void getAllItemRequestWithEmptyItems() {
        Sort createdSort = Sort.by("created").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, createdSort);

        when(requestRepository.findItemRequestByRequestorIdIsNot(user1.getId(), pageable)).thenReturn(List.of(request2));
        when(requestRepository.findById(request2.getId())).thenReturn(Optional.of(request2));
        when(itemRepository.findItemByRequestId(request1.getId())).thenReturn(List.of(item2));

        var result = requestService.getAllItemRequest(user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(request2.getId(), result.get(0).getId());
        Assertions.assertEquals(request2.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(0, result.get(0).getItems().size());
    }

    @Test
    public void getAllItemRequestEmptyList() {
        Sort createdSort = Sort.by("created").descending();
        Pageable pageable = FromSizeRequest.of(0, 10, createdSort);

        when(requestRepository.findItemRequestByRequestorIdIsNot(100L, pageable)).thenReturn(Collections.emptyList());

        var result = requestService.getAllItemRequest(100L, 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getItemRequestByRequestorId() {
        Sort createdSort = Sort.by("created").descending();

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findItemRequestByRequestorId(request1.getId(), createdSort)).thenReturn(List.of(request1));

        when(requestRepository.findById(request1.getId())).thenReturn(Optional.of(request1));
        when(itemRepository.findItemByListRequestIds(List.of(request1.getId()))).thenReturn(List.of(item1));

        var result = requestService.getItemRequestByRequestorId(user1.getId());

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(request1.getId(), result.get(0).getId());
        Assertions.assertEquals(request1.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(item1.getId(), result.get(0).getItems().get(0).getId());
        Assertions.assertEquals(item1.getName(), result.get(0).getItems().get(0).getName());
    }

    @Test
    public void getItemRequestByUnknownRequestorId() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getItemRequestByRequestorId(100L);
                });
    }

    @Test
    public void getItemRequestByRequestorIdWithEmptyItems() {
        Sort createdSort = Sort.by("created").descending();

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(requestRepository.findItemRequestByRequestorId(request2.getId(), createdSort)).thenReturn(List.of(request2));
        when(requestRepository.findById(request2.getId())).thenReturn(Optional.of(request2));
        when(itemRepository.findItemByRequestId(request2.getId())).thenReturn(Collections.emptyList());

        var result = requestService.getItemRequestByRequestorId(user2.getId());

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(request2.getId(), result.get(0).getId());
        Assertions.assertEquals(request2.getDescription(), result.get(0).getDescription());
        Assertions.assertEquals(0, result.get(0).getItems().size());
    }

    @Test
    public void getItemRequestByRequestorIdEmptyList() {
        Sort createdSort = Sort.by("created").descending();

        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(requestRepository.findItemRequestByRequestorId(user3.getId(), createdSort)).thenReturn(Collections.emptyList());

        var result = requestService.getItemRequestByRequestorId(user3.getId());

        assertNotNull(result);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    public void getItemRequestById() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findById(request1.getId())).thenReturn(Optional.of(request1));
        when(itemRepository.findItemByRequestId(request1.getId())).thenReturn(List.of(item1));

        var result = requestService.getItemRequestById(user1.getId(), request1.getId());

        Assertions.assertEquals(request1.getId(), result.getId());
        Assertions.assertEquals(request1.getDescription(), result.getDescription());
        Assertions.assertEquals(request1.getCreated(), result.getCreated());
        Assertions.assertEquals(request1.getId(), result.getItems().get(0).getRequestId());
    }

    @Test
    public void getItemRequestByIdWithEmptyItems() {
        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(requestRepository.findById(request2.getId())).thenReturn(Optional.of(request2));
        when(itemRepository.findItemByRequestId(request2.getId())).thenReturn(Collections.emptyList());

        var result = requestService.getItemRequestById(user2.getId(), request2.getId());

        Assertions.assertEquals(request2.getId(), result.getId());
        Assertions.assertEquals(request2.getDescription(), result.getDescription());
        Assertions.assertEquals(request2.getCreated(), result.getCreated());
        Assertions.assertEquals(0, result.getItems().size());
    }

    @Test
    public void getItemRequestByIdUnknownUserId() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getItemRequestById(100L, request1.getId());
                });
    }

    @Test
    public void getUnknownItemRequestById() {
        ItemRequest nullRequest = new ItemRequest();
        nullRequest = null;

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findById(99L)).thenReturn(Optional.ofNullable(nullRequest));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.getItemRequestById(user1.getId(), 99L);
                });
    }

    @Test
    public void saveNewRequest() {
        RequestInDto requestInDto = new RequestInDto("request1");

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.save(any())).thenReturn(request1);

        var result = requestService.saveNewRequest(user1.getId(), requestInDto);

        Assertions.assertEquals(request1.getId(), result.getId());
        Assertions.assertEquals(request1.getDescription(), result.getDescription());
        Assertions.assertEquals(request1.getCreated(), result.getCreated());
    }

    @Test
    public void saveNewRequestUnknownUserId() {
        RequestInDto requestInDto = new RequestInDto("request1");
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    requestService.saveNewRequest(100L, requestInDto);
                });
    }
}