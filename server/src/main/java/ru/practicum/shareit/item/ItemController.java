package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam(value = "from", defaultValue = "0") Integer from,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<ItemDto> itemDtos = itemService.getItemsByUserId(userId, from, size);
        log.info("Server: Get items, userId={}, number of items={} : {}", userId, itemDtos.size(), itemDtos);
        return itemDtos;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        ItemDto itemDto = itemService.getItemById(userId, itemId);
        log.info("Server: Get item {}, itemId={}, userId={}", itemDto, itemId, userId);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text) {
        if (text.isEmpty()) {
            log.info("Server: Get items with empty text");
            return new ArrayList<>();
        }
        List<ItemDto> itemDtos = itemService.searchItems(userId, text);
        log.info("Server: Get items with text={} : {}", text, itemDtos);
        return itemDtos;
    }

    @PostMapping
    public ItemInDto saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody ItemInDto itemInDto) {
        ItemInDto newItemDto = itemService.saveNewItem(userId, itemInDto);
        log.info("Server: Save new item {}, userId={}", newItemDto, userId);
        return newItemDto;
    }

    @PostMapping(value = "{itemId}/comment")
    public CommentDto saveNewComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                     @PathVariable("itemId") long itemId,
                                     @RequestBody CommentDto commentDto) {
        CommentDto newCommentDto = itemService.saveNewComment(bookerId, itemId, commentDto);
        log.info("Server: User added new comment {}, userId={}, itemId={}", newCommentDto, bookerId, itemId);
        return newCommentDto;
    }

    @PatchMapping("/{itemId}")
    public ItemInDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                @PathVariable long itemId,
                                @RequestBody ItemInDto itemInDto) {
        ItemInDto updateItemDto = itemService.updateItem(userId, itemId, itemInDto);
        log.info("Server: Update item data {}, itemId={}, userId={}", updateItemDto, itemId, userId);
        return updateItemDto;
    }
}
