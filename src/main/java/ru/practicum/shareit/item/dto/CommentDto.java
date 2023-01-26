package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;
    @NotBlank(groups = {Create.class}, message = "Комментарий не может быть пустым или содержать только пробелы")
    @NotNull(groups = {Create.class}, message = "Комментарий не задан")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
