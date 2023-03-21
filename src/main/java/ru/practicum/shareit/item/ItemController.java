package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ItemDto> itemDtos = itemService.getItemsByUserId(userId, from, size);
        log.info("Get -запрос:  Все бронирования from = {} size = {}", from, size);
        log.info("Get-запрос: у пользователя {} всего вещей={} : {}", userId, itemDtos.size(), itemDtos);
        return itemDtos;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (text.isEmpty()) {
            log.info("Get-запрос: нет текста для поиска ");
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = itemService.searchItems(text, from, size);
        log.info("Get -запрос:  Все запросы from = {} size = {}", from, size);
        log.info("Get-запрос: с текстом <{}> найдено: {}", text, itemDtos);
        return itemDtos;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        ItemDto itemDto = itemService.getItemById(userId, itemId);
        log.info("Get-запрос: вещь с id={}: {}", itemId, itemDto);
        return itemDto;
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                               @Valid @RequestBody ItemDto itemDto) {
        ItemDto newItemDto = itemService.saveNewItem(userId, itemDto);
        log.info("Post-запрос:  у пользователя {} новая вещь: {}", userId, newItemDto);
        return newItemDto;
    }

    @PostMapping(value = "{itemId}/comment")
    public CommentDto saveNewComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                     @PathVariable("itemId") long itemId,
                                     @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        CommentDto newCommentDto = itemService.saveNewComment(bookerId, itemId, commentDto);
        log.info("Post-запрос:  пользователь с id={} оставил отзыв на вещь с id={} : {}", bookerId, itemId, newCommentDto);
        return newCommentDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        ItemDto updateItemDto = itemService.updateItem(userId, itemId, itemDto);
        log.info("Patch-запрос: владелец с id={} обновил вещь с id={} : {}", userId, itemId, updateItemDto);
        return updateItemDto;
    }
}
