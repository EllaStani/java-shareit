package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner()
        );
    }

    public static List<ItemDto> mapToListItemDto(Iterable<Item> items) {
        List<ItemDto> itemDtos = new ArrayList<>();

        for (Item item : items) {
            itemDtos.add(mapToItemDto(item));
        }
        return itemDtos;
    }

    public static Item mapToItem(long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(userId);
        return item;
    }
}
