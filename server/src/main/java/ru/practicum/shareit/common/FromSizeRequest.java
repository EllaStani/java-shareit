package ru.practicum.shareit.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class FromSizeRequest extends PageRequest {
    private final int offset;

    protected FromSizeRequest(int offset, int size, Sort sort) {
        super(offset / size, size, sort);
        this.offset = offset;
    }

    public static FromSizeRequest of(int offset, int size, Sort sort) {
        return new FromSizeRequest(offset, size, sort);
    }

    public long getOffset() {
        return offset;
    }
}
