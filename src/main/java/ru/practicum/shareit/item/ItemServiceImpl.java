package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final CommentJpaRepository commentRepository;
    private final BookingJpaRepository bookingRepository;

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return mapToListItemDto(items);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        checkingExistUser(userId);
        Sort idSort = Sort.by("id");
        List<Item> userItems = itemRepository.findItemByOwnerId(userId, idSort);
        return mapToListItemDto(userItems);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        checkingExistItem(itemId);
        Item item = itemRepository.findById(itemId).get();
        List<Comment> comments = commentRepository.findCommentByItem_Id(itemId);
        ItemDto itemDto = ItemMapper.mapToItemDto(item, comments);

        if (item.getOwnerId() == userId) {
            itemDto.setLastBooking(getLastBookingByItemId(itemId));
            itemDto.setNextBooking(getNextBookingByItemId(itemId));
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        String lowerText = text.toLowerCase();
        List<Item> searchItems = itemRepository.search(lowerText);
        return ItemMapper.mapToListItemDto(searchItems);
    }

    @Transactional
    @Override
    public ItemDto saveNewItem(long userId, ItemDto itemDto) {
        checkingExistUser(userId);
        Item newItem = itemRepository.save(ItemMapper.mapToItem(userId, itemDto));
        return ItemMapper.mapToItemDto(newItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item updateItem = itemRepository.findById(itemId).get();
        if (updateItem.getOwnerId() == userId) {

            if (itemDto.getName() != null) {
                updateItem.setName(itemDto.getName());
            }

            if (itemDto.getDescription() != null) {
                updateItem.setDescription(itemDto.getDescription());
            }

            if (itemDto.getAvailable() != null) {
                updateItem.setAvailable(itemDto.getAvailable());
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
        List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBefore(
                bookerId, itemId, LocalDateTime.now());
        if (bookings.size() > 0) {
            Item item = itemRepository.findById(itemId).get();
            User author = userRepository.findById(bookerId).get();
            commentDto.setCreated(LocalDateTime.now());
            Comment newComment = commentRepository.save(ItemMapper.mapToComment(author, item, commentDto));
            return ItemMapper.mapToCommentDto(newComment);
        } else {
            throw new ValidationException(
                    String.format("Пользователь с id=%s не бронировал вещь с id=%s или срок брони не закончился",
                            bookerId, itemId));
        }
    }

    private void checkingExistUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
    }

    private void checkingExistItem(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException(String.format("Вещь с id=%s не найдена", itemId));
        }
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

    private BookingLastDto getLastBookingByItemId(long itemId) {
        Sort endSort = Sort.by("end").descending();
        List<Booking> lastBookings = bookingRepository.findBookingByItemIdAndEndIsBefore(
                itemId, LocalDateTime.now(), endSort);
        return lastBookings.size() == 0 ? null : BookingMapper.mapToBookingLastDto(lastBookings.get(0));
    }

    private BookingNextDto getNextBookingByItemId(long itemId) {
        Sort startSort = Sort.by("start").descending();
        List<Booking> nextBookings = bookingRepository.findBookingByItemIdAndStartIsAfter(
                itemId, LocalDateTime.now(), startSort);
        return nextBookings.size() == 0 ? null : BookingMapper.mapToBookingNextDto(nextBookings.get(0));
    }
}
