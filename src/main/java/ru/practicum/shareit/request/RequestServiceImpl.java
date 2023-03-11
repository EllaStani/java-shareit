package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemJpaRepository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.RequestInDto;
import ru.practicum.shareit.request.dto.RequestOutDto;
import ru.practicum.shareit.request.dto.RequestSaveDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserJpaRepository;
import ru.practicum.shareit.validation.FromSizeRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestJpaRepository requestRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    @Override
    public List<RequestOutDto> getAllItemRequest(long userId, Integer from, Integer size) {
        Sort createdSort = Sort.by("created").descending();
        Pageable pageable = FromSizeRequest.of(from, size, createdSort);
        List<ItemRequest> requests = requestRepository.findItemRequestByRequestorIdIsNot(userId, pageable);
        return mapToListRequestWithResponseDto(requests);
    }

    @Override
    public List<RequestOutDto> getItemRequestByRequestorId(long requestorId) {
        checkingExistUser(requestorId);
        Sort createdSort = Sort.by("created").descending();
        List<ItemRequest> requests = requestRepository.findItemRequestByRequestorId(requestorId, createdSort);
        return mapToListRequestWithResponseDto(requests);
    }

    @Override
    public RequestOutDto getItemRequestById(long userId, long requestId) {
        checkingExistUser(userId);
        ItemRequest request = checkingExistRequest(requestId);
        List<Item> items = itemRepository.findItemByRequestId(requestId);
        RequestOutDto requestDto = RequestMapper.mapToRequestOutDto(request,
                ItemMapper.mapToListItemForRequestDto(items));
        return requestDto;
    }

    @Transactional
    @Override
    public RequestSaveDto saveNewRequest(long userId, RequestInDto requestInDto) {
        User user = checkingExistUser(userId);
        ItemRequest request = RequestMapper.mapToItemRequest(requestInDto);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);
        ItemRequest newRequest = requestRepository.save(request);
        return RequestMapper.mapToRequestSaveDto(newRequest);
    }

    private User checkingExistUser(long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", userId));
        }
        return user;
    }

    private ItemRequest checkingExistRequest(long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId).orElse(null);
        if (itemRequest == null) {
            throw new NotFoundException(String.format("Запрос с id=%s не найден", requestId));
        }
        return itemRequest;
    }

    private RequestOutDto getRequestByIdWithResponses(long requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId).get();
        List<Item> items = itemRepository.findItemByRequestId(requestId);
        RequestOutDto requestDto = RequestMapper.mapToRequestOutDto(itemRequest,
                ItemMapper.mapToListItemForRequestDto(items));
        return requestDto;
    }

    private List<RequestOutDto> mapToListRequestWithResponseDto(List<ItemRequest> requests) {
        List<RequestOutDto> requestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestDtos.add(getRequestByIdWithResponses(request.getId()));
        }
        return requestDtos;
    }
}
