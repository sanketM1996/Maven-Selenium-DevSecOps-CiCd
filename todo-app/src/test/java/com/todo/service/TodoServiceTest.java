package com.todo.service;


import com.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TodoServiceTest {

    private TodoService todoService;

    @BeforeEach
    void setUp() {
        todoService = new TodoService();
    }

    @Test
    void shouldReturnInitialTodos() {

        List<Todo> todos = todoService.getAllTodos();

        assertNotNull(todos);

        assertEquals(3, todos.size());
    }

    @Test
    void shouldAddTodo() {

        int initialSize =
                todoService.getAllTodos().size();

        Todo todo =
                todoService.addTodo("Write Unit Tests");

        assertNotNull(todo.getId());

        assertEquals(
                "Write Unit Tests",
                todo.getTitle()
        );

        assertFalse(todo.isCompleted());

        assertEquals(
                initialSize + 1,
                todoService.getAllTodos().size()
        );
    }

    @Test
    void shouldToggleTodo() {

        Todo todo =
                todoService.addTodo("Test Toggle");

        assertFalse(todo.isCompleted());

        todoService.toggleTodo(todo.getId());

        assertTrue(todo.isCompleted());

        todoService.toggleTodo(todo.getId());

        assertFalse(todo.isCompleted());
    }

    @Test
    void shouldDeleteTodo() {

        Todo todo =
                todoService.addTodo("Delete Me");

        int sizeBeforeDelete =
                todoService.getAllTodos().size();

        todoService.deleteTodo(todo.getId());

        assertEquals(
                sizeBeforeDelete - 1,
                todoService.getAllTodos().size()
        );

        assertTrue(
                todoService.getAllTodos()
                        .stream()
                        .noneMatch(
                                t -> t.getId()
                                        .equals(todo.getId())
                        )
        );
    }
}