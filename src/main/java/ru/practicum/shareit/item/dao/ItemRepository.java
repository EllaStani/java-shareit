package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {

    List<Item> getAllItems();

    Item getItemById(long itemId);

    Item getItemByUserIdAndItemId(long userId, long itemId);

    List<Item> getItemsByUserId(long userId);


    List<Item> searchItems(String text);

    Item saveNewItem(User user, Item item);

    Item updateItem(long userId, long itemId, Item item);

    void deleteItemByUserIdAndItemId(long userId, long itemId);

}
