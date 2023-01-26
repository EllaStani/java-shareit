package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllItems();

    ItemDto getItemById(long userId, long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> searchItems(String text);

    ItemDto saveNewItem(long userId, ItemDto itemDto);

    CommentDto saveNewComment(long userId, long itemId, CommentDto commentDto);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

}
