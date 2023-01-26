package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setComments(new ArrayList<>());
        return itemDto;
    }

    public static ItemDto mapToItemDto(Item item, List<Comments> comments) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwnerId(item.getOwnerId());
        itemDto.setComments(mapToListCommentDto(comments));
        return itemDto;
    }

    public static List<ItemDto> mapToListItemDto(List<Item> items) {
        List<ItemDto> itemDtos = items.stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toList());
        return itemDtos;
    }

    public static Item mapToItem(long userId, ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(userId);
        return item;
    }

    public static CommentDto mapToCommentDto(Comments comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }

    public static List<CommentDto> mapToListCommentDto(List<Comments> comments) {
        List<CommentDto> commentDtos = comments.stream()
                .map(ItemMapper::mapToCommentDto)
                .collect(Collectors.toList());
        return commentDtos;
    }

    public static Comments mapToComment(User author, Item item, CommentDto commentDto) {
        Comments comment = new Comments();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }
}
