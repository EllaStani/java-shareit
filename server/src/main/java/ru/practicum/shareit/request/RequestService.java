package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;

import java.util.List;

public interface RequestService {
    List<RequestOutDto> getAllItemRequest(long userId, Integer from, Integer size);

    List<RequestOutDto> getItemRequestByRequestorId(long userId);

    RequestOutDto getItemRequestById(long userId, long requestId);

    RequestSaveDto saveNewRequest(long userId, RequestInDto requestInDto);
}
