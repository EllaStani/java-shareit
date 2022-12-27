package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> userItems = itemRepository.getAllItems();
        return userItems == null ? null : ItemMapper.mapToListItemDto(userItems);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return item == null ? null : ItemMapper.mapToItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        checkingExistUser(userId);
        List<Item> userItems = itemRepository.getItemsByUserId(userId);
        return userItems == null ? null : ItemMapper.mapToListItemDto(userItems);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> searchItems = itemRepository.searchItems(text);
        return searchItems == null ? null : ItemMapper.mapToListItemDto(searchItems);
    }

    @Override
    public ItemDto getItemByUserIdAndItemId(long userId, long itemId) {
        checkingExistUser(userId);
        Item userItem = itemRepository.getItemByUserIdAndItemId(userId, itemId);
        return userItem == null ? null : ItemMapper.mapToItemDto(userItem);
    }

    @Override
    public ItemDto saveNewItem(long userId, ItemDto itemDto) {
        checkingExistUser(userId);
        userService.getUserById(userId);
        Item item = itemRepository.saveNewItem(userId, ItemMapper.mapToItem(userId, itemDto));
        ;
        return item == null ? null : ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        checkingExistUser(userId);
        checkingExistItem(userId, itemId);
        Item updateItem = itemRepository.updateItem(userId, itemId, ItemMapper.mapToItem(userId, itemDto));
        return updateItem == null ? null : ItemMapper.mapToItemDto(updateItem);
    }

    @Override
    public void deleteItem(long userId, long itemId) {
        userService.getUserById(userId);
        itemRepository.deleteItemByUserIdAndItemId(userId, itemId);
    }

    private void checkingExistUser(long userId) {
        if (userService.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
    }

    private void checkingExistItem(long userId, long itemId) {
        if (itemRepository.getItemByUserIdAndItemId(userId, itemId) == null) {
            throw new NotFoundException(String.format("Вещь с id=%s не найдена", itemId));
        }
    }
}
