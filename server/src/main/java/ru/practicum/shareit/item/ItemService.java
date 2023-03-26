package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId, Integer from, Integer size);

    List<ItemDto> searchItems(long userId, String text);

    ItemInDto saveNewItem(long userId, ItemInDto itemInDto);

    CommentDto saveNewComment(long userId, long itemId, CommentDto commentDto);

    ItemInDto updateItem(long userId, long itemId, ItemInDto itemInDto);

}
