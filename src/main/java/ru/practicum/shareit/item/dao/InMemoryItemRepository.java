package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private long id = 0L;
    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public List<Item> getAllItems() {
        List<Item> allItems = new ArrayList<>();
        items.forEach((userId, userItems) -> allItems.addAll(userItems));
        return allItems;
    }

    @Override
    public Item getItemById(long itemId) {
        List<Item> allItems = getAllItems();
        if (allItems != null) {
            allItems.stream()
                    .filter(p -> p.getId().equals(itemId))
                    .collect(Collectors.toList());
            return allItems.get(0);
        }
        return null;
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item getItemByUserIdAndItemId(long userId, long itemId) {
        List<Item> userItems = items.get(userId);
        if (userItems != null) {
            userItems.stream()
                    .filter(p -> p.getId().equals(itemId))
                    .collect(Collectors.toList());
            return userItems.get(0);
        }
        return null;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> searchItems = getAllItems();
        if (searchItems != null) {
            return searchItems.stream()
                    .filter(p -> (p.getName().toLowerCase().contains(text.toLowerCase())
                            || p.getDescription().toLowerCase().contains(text.toLowerCase()))
                            && p.getAvailable())
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Item saveNewItem(long userId, Item item) {
        long newId = generateId();
        item.setId(newId);
        item.setOwner(userId);
        items.compute(userId, (id, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item updateItem(long userId, long itemId, Item item) {
        Item updateItem = getItemByUserIdAndItemId(userId, itemId);

        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return updateItem;
    }

    @Override
    public void deleteItemByUserIdAndItemId(long userId, long itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    private Long generateId() {
        return ++this.id;
    }

}
