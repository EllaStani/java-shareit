package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item");
        itemDto.setDescription("ItemDescription");
        itemDto.setAvailable(true);
    }

    @Test
    public void getItemsByUserId() throws Exception {
        when(itemService.getItemsByUserId(1L, 0, 10))
                .thenReturn(List.of(itemDto));
        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
        verify(itemService, times(1))
                .getItemsByUserId(1L, 0, 10);
    }

    @Test
    public void getItemsUnknownUser() throws Exception {
        when(itemService.getItemsByUserId(100L, 0, 10))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 100L))
                .andExpect(status().is(404));
    }

    @Test
    public void searchItems() throws Exception {
        when(itemService.searchItems(1L, "tem"))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", "tem")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    public void searchItemsWithEmptyText() throws Exception {
        when(itemService.searchItems(1L, ""))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", "")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
        verify(itemService, times(0)).searchItems(1L, "");
    }

    @Test
    public void searchItemsWithEmptyList() throws Exception {
        when(itemService.searchItems(1L, "abc"))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text={text}", "abc")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(0)));
        verify(itemService, times(1))
                .searchItems(1L, "abc");
    }

    @Test
    public void getItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    public void getUnknownItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong())).thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", 100L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().is(404));
        verify(itemService, times(1))
                .getItemById(anyLong(), anyLong());
    }

    @Test
    public void saveNewItem() throws Exception {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setId(1L);
        itemInDto.setName("Item");
        itemInDto.setDescription("ItemDescription");
        itemInDto.setAvailable(true);

        ItemInDto newItemDto = new ItemInDto();
        newItemDto.setName("Item");
        newItemDto.setDescription("ItemDescription");
        newItemDto.setAvailable(true);

        when(itemService.saveNewItem(1L, newItemDto))
                .thenReturn(itemInDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1))
                .saveNewItem(1L, newItemDto);
    }

    @Test
    public void saveNewItemUnknownUser() throws Exception {
        ItemInDto newItemDto = new ItemInDto();
        newItemDto.setName("Item");
        newItemDto.setDescription("ItemDescription");
        newItemDto.setAvailable(true);

        when(itemService.saveNewItem(100L, newItemDto))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
        verify(itemService, times(1))
                .saveNewItem(100L, newItemDto);
    }

    @Test
    public void saveNewComment() throws Exception {
        CommentDto newCommentDto = new CommentDto();
        newCommentDto.setText("Comment from user1");

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment from user1");
        commentDto.setAuthorName("user1");
        commentDto.setCreated(LocalDateTime.parse("2023-03-01T00:09:00"));

        when(itemService.saveNewComment(1L, 1L, newCommentDto))
                .thenReturn(commentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(commentDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(commentDto.getText()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.authorName").value(commentDto.getAuthorName()));
        verify(itemService, times(1))
                .saveNewComment(1L, 1L, newCommentDto);
    }

    @Test
    public void saveNewCommentUnknownUser() throws Exception {
        CommentDto newCommentDto = new CommentDto();
        newCommentDto.setText("Comment from user1");

        when(itemService.saveNewComment(100L, 1L, newCommentDto))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newCommentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

        verify(itemService, times(1))
                .saveNewComment(100L, 1L, newCommentDto);
    }

    @Test
    public void updateItem() throws Exception {
        ItemInDto itemInDto = new ItemInDto();
        itemInDto.setId(1L);
        itemInDto.setName("Item");
        itemInDto.setDescription("ItemDescription");
        itemInDto.setAvailable(true);

        ItemInDto newItemDto = new ItemInDto();
        newItemDto.setDescription("UpdateItemDescription");

        when(itemService.updateItem(1L, 1L, newItemDto))
                .thenReturn(itemInDto);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(itemDto.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.available").value(itemDto.getAvailable()));
        verify(itemService, times(1))
                .updateItem(1L, 1L, newItemDto);
    }

    @Test
    public void updateItemUnknownUser() throws Exception {
        ItemInDto newItemDto = new ItemInDto();
        newItemDto.setDescription("ItemDescription");

        when(itemService.updateItem(100L, 1L, newItemDto))
                .thenThrow(new NotFoundException("Not Found"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 100L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(mapper.writeValueAsString(newItemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));

        verify(itemService, times(1)).updateItem(100L, 1L, newItemDto);
    }
}