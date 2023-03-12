package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Integer from, Integer size);

    ItemDto saveNewItem(long userId, ItemDto itemDto);

    CommentDto saveNewComment(long userId, long itemId, CommentDto commentDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

}
