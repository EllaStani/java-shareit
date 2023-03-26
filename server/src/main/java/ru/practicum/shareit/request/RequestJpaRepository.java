package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findItemRequestByRequestorId(long requestorId, Sort sort);

    List<ItemRequest> findItemRequestByRequestorIdIsNot(long requestorId, Pageable pageable);
}
