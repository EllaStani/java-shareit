package ru.practicum.shareit.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ErrorResponse {
    private String errorMessage;

    public ErrorResponse(String error) {
        this.errorMessage = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
