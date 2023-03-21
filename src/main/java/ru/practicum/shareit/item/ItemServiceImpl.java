package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingJpaRepository;
import ru.practicum.shareit.booking.BookingMapper;
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
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final RequestJpaRepository requestRepository;
    private final CommentJpaRepository commentRepository;
    private final BookingJpaRepository bookingRepository;

    @Override
    public List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size) {
        checkingExistUser(userId);
        Sort idSort = Sort.by("id");
        Pageable pageable = FromSizeRequest.of(from, size, idSort);
        List<Item> userItems = itemRepository.findItemByOwnerId(userId, pageable);
        return mapToListItemDto(userItems);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = checkingExistItem(itemId);
        List<Comment> comments = commentRepository.findCommentByItem_Id(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, comments);

        if (item.getOwner().getId() == userId) {
            itemDto.setLastBooking(getLastBookingByItemId(itemId));
            itemDto.setNextBooking(getNextBookingByItemId(itemId));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer from, Integer size) {
        String lowerText = text.toLowerCase();
        Pageable pageable = FromSizeRequest.of(from, size, Sort.unsorted());
        List<Item> searchItems = itemRepository.search(lowerText, pageable);
        return ItemMapper.mapToListItemDto(searchItems);
    }

    @Transactional
    @Override
    public ItemDto saveNewItem(long userId, ItemDto itemDto) {
        User user = checkingExistUser(userId);
        Item newItem = itemRepository.save(mapToItem(user, itemDto));
        return ItemMapper.mapToItemDto(newItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item updateItem = checkingExistItem(itemId);
        if (updateItem.getOwner().getId() == userId) {

            if (itemDto.getName() != null) {
                updateItem.setName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                updateItem.setDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                updateItem.setAvailable(itemDto.getAvailable());
            }

            if (itemDto.getRequestId() != null) {
                ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId()).get();
                updateItem.setRequest(itemRequest);
            }

            itemRepository.save(updateItem);
            return ItemMapper.mapToItemDto(updateItem);
        } else {
            throw new NotFoundException(String.format("Пользователь с id=%s не владелец вещи с id=%s",
                    userId, itemId));
        }
    }

    @Transactional
    @Override
    public CommentDto saveNewComment(long bookerId, long itemId, CommentDto commentDto) {
        User author = checkingExistUser(bookerId);
        Item item = checkingExistItem(itemId);

        LocalDateTime nowDate = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(
                bookerId, itemId, nowDate);
        if (bookings.size() > 0) {
            commentDto.setCreated(nowDate);
            Comment newComment = commentRepository.save(ItemMapper.mapToComment(author, item, commentDto));
            return ItemMapper.mapToCommentDto(newComment);
        } else {
            throw new ValidationException(
                    String.format("Пользователь с id=%s не бронировал вещь с id=%s или срок брони не закончился",
                            bookerId, itemId));
        }
    }

    private User checkingExistUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private Item checkingExistItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%s не найдена", itemId)));
    }

    private List<ItemDto> mapToListItemDto(List<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            List<Comment> comments = commentRepository.findCommentByItem_Id(item.getId());
            ItemDto itemDto = ItemMapper.mapToItemDto(item, comments);
            itemDto.setLastBooking(getLastBookingByItemId(item.getId()));
            itemDto.setNextBooking(getNextBookingByItemId(item.getId()));
            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    private Item mapToItem(User user, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId()).orElse(null);
            item.setRequest(itemRequest);
        }
        return item;
    }

    private BookingLastDto getLastBookingByItemId(long itemId) {
        Sort startSort = Sort.by("start").descending();
        LocalDateTime nowDate = LocalDateTime.now();
        List<Booking> lastBookings = bookingRepository.findBookingByItemIdAndStartIsBefore(
                itemId, nowDate, startSort);
        return lastBookings.size() == 0 ? null : BookingMapper.mapToBookingLastDto(lastBookings.get(0));
    }

    private BookingNextDto getNextBookingByItemId(long itemId) {
        Sort startSort = Sort.by("start");
        LocalDateTime nowDate = LocalDateTime.now();
        List<Booking> nextBookings = bookingRepository.findBookingByItemIdAndStartIsAfterAndStatusIsNot(
                itemId, nowDate, REJECTED, startSort);
        return nextBookings.size() == 0 ? null : BookingMapper.mapToBookingNextDto(nextBookings.get(0));
    }
}
