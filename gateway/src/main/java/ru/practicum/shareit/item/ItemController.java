package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {

        log.info("Get items, userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {

        log.info("Get itemId={}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestParam String text) {

        log.info("Get items with text={}", text);
        return itemClient.searchItems(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody ItemInDto itemInDto) {

        log.info("Creating new item {}, userId={}", itemInDto, userId);
        return itemClient.saveNewItem(userId, itemInDto);
    }

    @PostMapping(value = "{itemId}/comment")
    public ResponseEntity<Object> saveNewComment(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @PathVariable("itemId") long itemId,
                                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {

        log.info("User add new comment {}, userId={}, itemId={} ", commentDto, bookerId, itemId);
        return itemClient.saveNewComment(bookerId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemInDto itemInDto) {

        log.info("Update item data {}, itemId={}, userId={}", itemInDto, itemId, userId);
        return itemClient.updateItem(userId, itemId, itemInDto);
    }
}
