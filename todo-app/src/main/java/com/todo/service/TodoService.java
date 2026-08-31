package com.todo.service;

import com.todo.model.Todo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TodoService {

    private final List<Todo> todos = new ArrayList<>();

    private final AtomicLong idGenerator = new AtomicLong(1);

    public TodoService() {

        todos.add(new Todo(
                idGenerator.getAndIncrement(),
                "Learn Spring Boot",
                false
        ));

        todos.add(new Todo(
                idGenerator.getAndIncrement(),
                "Learn Thymeleaf",
                false
        ));

        todos.add(new Todo(
                idGenerator.getAndIncrement(),
                "Build Todo App",
                true
        ));
    }

    public List<Todo> getAllTodos() {
        return new ArrayList<>(todos);
    }

    public Todo addTodo(String title) {

        Todo todo = new Todo(
                idGenerator.getAndIncrement(),
                title,
                false
        );

        todos.add(todo);

        return todo;
    }

    public void toggleTodo(Long id) {

        todos.stream()
                .filter(todo -> todo.getId().equals(id))
                .findFirst()
                .ifPresent(todo ->
                        todo.setCompleted(!todo.isCompleted())
                );
    }

    public void deleteTodo(Long id) {

        todos.removeIf(todo ->
                todo.getId().equals(id)
        );
    }
}