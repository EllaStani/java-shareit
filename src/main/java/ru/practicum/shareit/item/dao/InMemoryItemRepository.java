package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemRepository implements ItemRepository {
    private long id = 0L;
    private final Map<Long, List<Item>> itemsOfUser = new HashMap<>();
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<Item>(items.values());
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        return itemsOfUser.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item getItemByUserIdAndItemId(long userId, long itemId) {
        List<Item> userItems = itemsOfUser.get(userId);
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
        String lowerText = text.toLowerCase();
        List<Item> searchItems = getAllItems();
        if (searchItems != null) {
            return searchItems.stream()
                    .filter(p -> (p.getName().toLowerCase().contains(lowerText)
                            || p.getDescription().toLowerCase().contains(lowerText))
                            && p.getAvailable())
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public Item saveNewItem(User user, Item item) {
        long newId = generateId();
        item.setId(newId);
        item.setOwner(user);
        itemsOfUser.compute(user.getId(), (id, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            items.put(newId, item);
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
        if (itemsOfUser.containsKey(userId)) {
            List<Item> userItems = itemsOfUser.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
            items.remove(itemId);
        }
    }

    private Long generateId() {
        return ++this.id;
    }

}
