package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> itemDtos = itemService.getItemsByUserId(userId);
        log.info("Get-запрос: у пользователя {} всего вещей={} : {}", userId, itemDtos.size(), itemDtos);
        return itemDtos;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        if (text.isEmpty()) {
            log.info("Get-запрос: нет текста для поиска ");
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = itemService.searchItems(text);
        log.info("Get-запрос: с текстом <{}> найдено: {}", text, itemDtos);
        return itemDtos;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemByUserIdAndItemId(@PathVariable long itemId) {
        ItemDto itemDto = itemService.getItemById(itemId);
        log.info("Get-запрос: вещь с id={}: {}", itemId, itemDto);
        return itemDto;
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        validationCreateItem(userId, itemDto);
        ItemDto newItemDto = itemService.saveNewItem(userId, itemDto);
        log.info("Post-запрос:  у пользователя {} новая вещь: {}", userId, newItemDto);
        return newItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        ItemDto updateItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.info("Patch-запрос: пользователь {} обновил вещь с id={} : {}", userId, itemId, updateItemDto);
        return updateItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        itemService.deleteItem(userId, itemId);
    }

    private void validationCreateItem(Long userId, ItemDto itemDto) {
        if (userId == null) {
            log.error("Post-запрос не выполнен: id пользователя не задано");
            throw new ValidationException("Не задано id пользователя");
        }

        if (!StringUtils.hasText(itemDto.getName())) {
            log.error("Post-запрос не выполнен: наименование либо пусто, либо содержит только пробелы");
            throw new ValidationException("наименование вещи не может быть пустым и содержать только пробелы");
        }

        if (!StringUtils.hasText(itemDto.getDescription())) {
            log.error("Post-запрос не выполнен: описание либо пусто, либо содержит только пробелы");
            throw new ValidationException("описание вещи не может быть пустым и содержать только пробелы");
        }

        if (itemDto.getAvailable() == null) {
            log.error("Post-запрос не выполнен: не задан статус доступности для аренды");
            throw new ValidationException("Не задан статус доступности для аренды");
        }
    }
}
