package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingLastDto;
import ru.practicum.shareit.booking.dto.BookingNextDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.RequestJpaRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.validation.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static ru.practicum.shareit.booking.BookingStatus.CANCELED;

public class ItemServiceImplTest {
    private ItemJpaRepository itemRepository;
    private UserJpaRepository userRepository;
    private RequestJpaRepository requestRepository;
    private CommentJpaRepository commentRepository;
    private BookingJpaRepository bookingRepository;
    private ItemService itemService;
    private User user1;
    private User user2;
    private User user3;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;
    private ItemDto itemDto1;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemJpaRepository.class);
        userRepository = mock(UserJpaRepository.class);
        requestRepository = mock(RequestJpaRepository.class);
        commentRepository = mock(CommentJpaRepository.class);
        bookingRepository = mock(BookingJpaRepository.class);

        itemService = new ItemServiceImpl(
                itemRepository, userRepository, requestRepository, commentRepository, bookingRepository);

        user1 = new User(1L, "user1", "user1@yandex.ru");
        user2 = new User(2L, "user2", "user2@yandex.ru");
        user3 = new User(3L, "user3", "user3@yandex.ru");

        item1 = new Item(1L, "item1", "itemDescription1", true, user1, null);
        item2 = new Item(2L, "item2", "itemDescription2", true, user2, null);

        booking1 = new Booking(1L, LocalDateTime.parse("2023-02-01T12:00:00"),
                LocalDateTime.parse("2023-02-03T14:00:00"), item1, user2, BookingStatus.APPROVED);
        booking2 = new Booking(2L, LocalDateTime.parse("2023-05-01T12:00:00"),
                LocalDateTime.parse("2023-05-03T00:09:00"), item1, user3, CANCELED);

        itemDto1 = makeItemDto("item1", "itemDescription1", true);
    }

    @Test
    public void getItemsByUserId() {
        BookingLastDto lastBooking = new BookingLastDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);

        BookingNextDto nextBooking = new BookingNextDto();
        nextBooking.setId(2L);
        nextBooking.setBookerId(3L);

        itemDto1.setId(item1.getId());
        itemDto1.setOwner(user1.getId());
        itemDto1.setComments(Collections.emptyList());
        itemDto1.setLastBooking(lastBooking);
        itemDto1.setNextBooking(nextBooking);

        Sort idSort = Sort.by("id");
        Pageable pageable = FromSizeRequest.of(0, 10, idSort);

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.findItemByOwnerId(user1.getId(), pageable)).thenReturn(List.of(item1));
        when(commentRepository.findCommentByItem_Id((item1.getId()))).thenReturn(Collections.emptyList());
        when(bookingRepository.findBookingByItemIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingByItemIdAndStartIsAfterAndStatusIsNot(
                anyLong(), any(LocalDateTime.class), any(), any()))
                .thenReturn(List.of(booking2));

        var result = itemService.getItemsByUserId(user1.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(itemDto1, result.get(0));
    }

    @Test
    public void getItemsByUnknownUserId() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.getItemsByUserId(user1.getId(), 0, 10);
                });
    }

    @Test
    public void getItemsByUserIdWithEmptyItems() {
        Sort idSort = Sort.by("id");
        Pageable pageable = FromSizeRequest.of(0, 10, idSort);

        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(itemRepository.findItemByOwnerId(user3.getId(), pageable)).thenReturn(Collections.emptyList());

        var result = itemService.getItemsByUserId(user3.getId(), 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(0, result.size());
        verify(commentRepository, never())
                .findCommentByItem_Id(anyLong());
        verify(bookingRepository, never())
                .findBookingByItemIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any());
        verify(bookingRepository, never())
                .findBookingByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any());
    }

    @Test
    public void getItemById() {
        BookingLastDto lastBooking = new BookingLastDto();
        lastBooking.setId(1L);
        lastBooking.setBookerId(2L);

        BookingNextDto nextBooking = new BookingNextDto();
        nextBooking.setId(1L);
        nextBooking.setBookerId(3L);

        itemDto1.setOwner(user1.getId());
        itemDto1.setLastBooking(lastBooking);
        itemDto1.setNextBooking(nextBooking);

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(commentRepository.findCommentByItem_Id((item1.getId()))).thenReturn(Collections.emptyList());
        when(bookingRepository.findBookingByItemIdAndEndIsBefore(
                anyLong(), any(LocalDateTime.class), any()))
                .thenReturn(List.of(booking1));
        when(bookingRepository.findBookingByItemIdAndStartIsAfterAndStatusIsNot(
                anyLong(), any(LocalDateTime.class), any(), any()))
                .thenReturn(List.of(booking2));

        var result = itemService.getItemById(user1.getId(), item1.getId());

        Assertions.assertEquals(item1.getId(), result.getId());
        Assertions.assertEquals(item1.getAvailable(), result.getAvailable());
        Assertions.assertEquals(user1.getId(), result.getOwner());
        Assertions.assertEquals(null, result.getRequestId());
    }

    @Test
    public void getUnknownItemById() {
        Item nullItem = new Item();
        nullItem = null;

        when(itemRepository.findById(99L)).thenReturn(Optional.ofNullable(nullItem));
        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.getItemById(1L, 99L);
                });
    }

    @Test
    public void searchItems() {
        ItemDto itemDto2 = makeItemDto("item2", "itemDescription2", true);
        List<ItemDto> itemDtos = List.of(itemDto1, itemDto2);
        Pageable pageable = FromSizeRequest.of(0, 10, Sort.unsorted());

        when(itemRepository.search("tion", pageable)).thenReturn(List.of(item1, item2));

        var result = itemService.searchItems("tion", 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(itemDtos.get(0).getName(), result.get(0).getName());
        Assertions.assertEquals(itemDtos.get(1).getName(), result.get(1).getName());
        verify(itemRepository, times(1)).search("tion", pageable);
    }

    @Test
    public void searchItemsWithEmptyResult() {
        Pageable pageable = FromSizeRequest.of(0, 10, Sort.unsorted());
        when(itemRepository.search("abc", pageable)).thenReturn(Collections.emptyList());

        var result = itemService.searchItems("abc", 0, 10);

        assertNotNull(result);
        Assertions.assertEquals(0, result.size());
        verify(itemRepository, times(1)).search("abc", pageable);
    }

    @Test
    public void saveNewItemWithoutRequest() {
        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(itemRepository.save(any())).thenReturn(item1);

        var result = itemService.saveNewItem(user1.getId(), itemDto1);

        Assertions.assertEquals(item1.getId(), result.getId());
        Assertions.assertEquals(item1.getAvailable(), result.getAvailable());
        Assertions.assertEquals(user1.getId(), result.getOwner());
        Assertions.assertEquals(null, result.getRequestId());
    }

    @Test
    public void saveNewItemOnRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        item1.setRequest(request);
        itemDto1.setRequestId(request.getId());

        when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
        when(requestRepository.findById(itemDto1.getRequestId())).thenReturn(Optional.of(request));
        when(itemRepository.save(any())).thenReturn(item1);

        var result = itemService.saveNewItem(user1.getId(), itemDto1);

        Assertions.assertEquals(item1.getId(), result.getId());
        Assertions.assertEquals(item1.getAvailable(), result.getAvailable());
        Assertions.assertEquals(user1.getId(), result.getOwner());
        Assertions.assertEquals(request.getId(), result.getRequestId());
    }

    @Test
    public void saveNewItemUnknownUser() {
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.saveNewItem(100L, itemDto1);
                });
    }

    @Test
    public void updateItem() {
        ItemDto updateItemDto = makeItemDto("updateItem", "updateDescription", false);

        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));

        var result = itemService.updateItem(user1.getId(), item1.getId(), updateItemDto);

        Assertions.assertEquals(item1.getId(), result.getId());
        Assertions.assertEquals("updateItem", result.getName());
        Assertions.assertEquals("updateDescription", result.getDescription());
        Assertions.assertEquals(false, result.getAvailable());
        Assertions.assertEquals(user1.getId(), result.getOwner());
    }

    @Test
    public void updateItemUnknownUser() {
        ItemDto updateItemDto = makeItemDto("updateItem", "updateDescription", false);
        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.updateItem(100L, item1.getId(), updateItemDto);
                });
    }

    @Test
    public void updateUnknownItem() {
        Item nullItem = new Item();
        nullItem = null;
        ItemDto updateItemDto = makeItemDto("updateItem", "updateDescription", false);

        when(itemRepository.findById(99L)).thenReturn(Optional.ofNullable(nullItem));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.updateItem(user1.getId(), 99L, updateItemDto);
                });
    }

    @Test
    public void saveNewComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("comment from user2");
        comment.setAuthor(user2);
        comment.setCreated(LocalDateTime.parse("2023-03-01T00:09:00"));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment from user2");
        commentDto.setAuthorName(user2.getName());

        when(userRepository.findById(user2.getId())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(
                anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking1));

        when(commentRepository.save(any())).thenReturn(comment);

        var result = itemService.saveNewComment(user2.getId(), item1.getId(), commentDto);

        assertNotNull(result);
        Assertions.assertEquals(comment.getId(), result.getId());
        Assertions.assertEquals(comment.getText(), result.getText());
        Assertions.assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        Assertions.assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    public void saveNewCommentNoBookings() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment from user3");

        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(itemRepository.findById(item1.getId())).thenReturn(Optional.of(item1));
        when(bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(
                anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Assertions.assertThrows(ValidationException.class, () -> {
            itemService.saveNewComment(3L, 1L, commentDto);
        });
    }

    @Test
    public void saveNewCommentUnknownUser() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment from user100");

        User nullUser = new User();
        nullUser = null;

        when(userRepository.findById(100L)).thenReturn(Optional.ofNullable(nullUser));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.saveNewComment(100L, 1L, commentDto);
                });
    }

    @Test
    public void saveNewCommentUnknownItem() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("comment from user3");

        Item nullItem = new Item();
        nullItem = null;

        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        when(itemRepository.findById(99L)).thenReturn(Optional.ofNullable(nullItem));

        Assertions.assertThrows(NotFoundException.class,
                () -> {
                    itemService.saveNewComment(3L, 1L, commentDto);
                });
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }
}