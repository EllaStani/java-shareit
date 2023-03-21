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
import java.util.stream.Collectors;

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
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
    }

    private ItemRequest checkingExistRequest(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%s не найден", requestId)));
    }

    private List<RequestOutDto> mapToListRequestWithResponseDto(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream().map(ItemRequest::getId).collect(Collectors.toList());
        List<Item> items = itemRepository.findItemByListRequestIds(requestIds);

        List<RequestOutDto> requestDtos = new ArrayList<>();
        for (ItemRequest request : requests) {
            List requestItems = items.stream()
                    .filter(i -> i.getRequest().getId().equals(request.getId()))
                    .collect(Collectors.toList());
            requestDtos.add(
                    RequestMapper.mapToRequestOutDto(request, ItemMapper.mapToListItemForRequestDto(requestItems)));
        }
        return requestDtos;
    }
}
