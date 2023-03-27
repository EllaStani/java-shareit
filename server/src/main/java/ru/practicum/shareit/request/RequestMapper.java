package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;
import ru.practicum.shareit.request.dto.RequestOutDto;

import java.util.List;

public class RequestMapper {
    public static RequestSaveDto mapToRequestSaveDto(ItemRequest request) {
        RequestSaveDto requestSaveDto = new RequestSaveDto(
                request.getId(),
                request.getCreated(),
                request.getDescription());
        return requestSaveDto;
    }

    public static RequestOutDto mapToRequestOutDto(ItemRequest request, List<ItemForRequestDto> items) {
        RequestOutDto requestDto = new RequestOutDto(
                request.getId(),
                request.getCreated(),
                request.getDescription(),
                items);
        return requestDto;
    }

    public static ItemRequest mapToItemRequest(RequestInDto requestInDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestInDto.getDescription());
        return request;
    }
}
