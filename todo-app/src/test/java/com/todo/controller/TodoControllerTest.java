package com.todo.controller;


import com.todo.service.TodoService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;


    @Test
    void shouldDisplayTodoPage()
            throws Exception {

        when(todoService.getAllTodos())
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("todos"))
                .andExpect(model().attributeExists("todos"));

        verify(todoService)
                .getAllTodos();
    }


    @Test
    void shouldAddTodo()
            throws Exception {

        mockMvc.perform(
                        post("/todos")
                                .param(
                                        "title",
                                        "Learn Spring Boot"
                                )
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/")
                );

        verify(todoService)
                .addTodo("Learn Spring Boot");
    }


    @Test
    void shouldToggleTodo()
            throws Exception {

        mockMvc.perform(
                        post("/todos/1/toggle")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/")
                );

        verify(todoService)
                .toggleTodo(1L);
    }


    @Test
    void shouldDeleteTodo()
            throws Exception {

        mockMvc.perform(
                        post("/todos/1/delete")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/")
                );

        verify(todoService)
                .deleteTodo(1L);
    }


    @Test
    void shouldNotAddBlankTodo()
            throws Exception {

        mockMvc.perform(
                        post("/todos")
                                .param("title", "   ")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        redirectedUrl("/")
                );

        verify(todoService, never())
                .addTodo(anyString());
    }
}