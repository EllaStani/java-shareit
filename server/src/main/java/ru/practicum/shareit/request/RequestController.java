package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/{requestId}")
    public RequestOutDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @PathVariable("requestId") long requestId) {
        RequestOutDto itemRequest = requestService.getItemRequestById(userId, requestId);
        log.info("Server: Get itemRequest {}, userId={}", itemRequest, requestId);
        return itemRequest;
    }

    @GetMapping
    public List<RequestOutDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<RequestOutDto> requests = requestService.getItemRequestByRequestorId(userId);
        log.info("Server: Get requests {}, userId={}, number of requests={}",
                requests, userId, requests.size());
        return requests;
    }

    @GetMapping(value = "/all")
    public List<RequestOutDto> getRequestsAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        List<RequestOutDto> requests = requestService.getAllItemRequest(userId, from, size);
        log.info("Server: Get all requests for userId={} : - {}", userId, requests);
        return requests;
    }

    @PostMapping
    public RequestSaveDto saveNewItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody RequestInDto requestInDto) {
        RequestSaveDto newItemRequest = requestService.saveNewRequest(userId, requestInDto);
        log.info("Server: Save new request {}, userId={}", newItemRequest, userId);
        return newItemRequest;
    }
}
