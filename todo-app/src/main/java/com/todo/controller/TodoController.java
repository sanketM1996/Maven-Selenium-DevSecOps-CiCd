package com.todo.controller;

import com.todo.service.TodoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute(
                "todos",
                todoService.getAllTodos()
        );

        return "todos";
    }

    @PostMapping("/todos")
    public String addTodo(
            @RequestParam String title
    ) {

        if (title != null && !title.trim().isEmpty()) {

            todoService.addTodo(title.trim());
        }

        return "redirect:/";
    }

    @PostMapping("/todos/{id}/toggle")
    public String toggleTodo(
            @PathVariable Long id
    ) {

        todoService.toggleTodo(id);

        return "redirect:/";
    }

    @PostMapping("/todos/{id}/delete")
    public String deleteTodo(
            @PathVariable Long id
    ) {

        todoService.deleteTodo(id);

        return "redirect:/";
    }
}