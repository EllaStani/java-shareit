package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findItemByOwnerId(long userId, Pageable pageable);

    List<Item> findItemByRequestId(long requestId);

    @Query(" select i from Item i " +
            "where (lower(i.name) like upper(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%'))) and i.available = true")
    List<Item> search(String text, Pageable pageable);
}
