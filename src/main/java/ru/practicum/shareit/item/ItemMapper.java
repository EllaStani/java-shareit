package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        itemDto.setComments(new ArrayList<>());
        return itemDto;
    }

    public static ItemDto mapToItemDto(Item item, List<Comment> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner().getId());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        itemDto.setComments(mapToListCommentDto(comments));
        return itemDto;
    }

    public static List<ItemDto> mapToListItemDto(List<Item> items) {
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
        return itemDtos;
    }

    public static ItemForRequestDto mapToItemForRequestDto(Item item) {
        ItemForRequestDto itemDto = new ItemForRequestDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getRequest().getId());
        itemDto.setOwnerId(item.getOwner().getId());
        return itemDto;
    }

    public static List<ItemForRequestDto> mapToListItemForRequestDto(List<Item> items) {
        List<ItemForRequestDto> itemDtos = items.stream()
                .map(ItemMapper::mapToItemForRequestDto)
                .collect(Collectors.toList());
        return itemDtos;
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapToListCommentDto(List<Comment> comments) {
        List<CommentDto> commentDtos = comments.stream()
                .map(ItemMapper::mapToCommentDto)
                .collect(Collectors.toList());
        return commentDtos;
    }

    public static Comment mapToComment(User author, Item item, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(commentDto.getCreated());
        return comment;
    }
}
