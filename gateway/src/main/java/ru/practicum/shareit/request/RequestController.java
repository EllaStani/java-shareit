package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable("requestId") long requestId) {
        log.info("Get requestId={}, userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get itemRequests {}, userId={}", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Object> getRequestsAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {

        log.info("Get all requests for userId={}, from={}, size={}", userId, from, size);
        return requestClient.getRequestAll(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Validated({Create.class}) @RequestBody RequestInDto requestInDto) {
        log.info("Creating new request {}, userId={}", requestInDto, userId);
        return requestClient.saveNewRequest(userId, requestInDto);
    }
}
