package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestOutDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<RequestOutDto> requests = requestService.getItemRequestByRequestorId(userId);
        log.info("Get -запрос:  У пользователя с id = {} всего запросов {} : - {}",
                userId, requests.size(), requests);
        return requests;
    }

    @GetMapping("/{requestId}")
    public RequestOutDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable("requestId") long requestId) {
        RequestOutDto itemRequest = requestService.getItemRequestById(userId, requestId);
        log.info("Get-запрос: Запрос с id={} : - {}", requestId, itemRequest);
        return itemRequest;
    }

    @GetMapping(value = "/all")
    public List<RequestOutDto> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId,
                    @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                    @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {

        List<RequestOutDto> requests = requestService.getAllItemRequest(userId, from, size);
        log.info("Get -запрос:  Все запросы from = {} size = {}", from, size);
        log.info("Get -запрос:  Для пользователя с id = {} все запросы начиная с {} : - {}", userId,
                from, requests);
        return requests;
    }

    @PostMapping
    public RequestSaveDto saveNewItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Validated({Create.class}) @RequestBody RequestInDto requestInDto) {
        RequestSaveDto newItemRequest = requestService.saveNewRequest(userId, requestInDto);
        log.info("Post-запрос:  у пользователя {} новый запрос: {}", userId, newItemRequest);
        return newItemRequest;
    }
}
