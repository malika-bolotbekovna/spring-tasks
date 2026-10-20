package com.example.demo.controller;

import com.example.demo.model.TaskModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {

    private final List<TaskModel> tasks = new ArrayList<>();
    private long nextId = 1;

    public TaskController() {
        TaskModel task = new TaskModel();
        task.setId(nextId++);
        task.setName("Первая задача");
        task.setDescription("Изучить Spring MVC и Thymeleaf");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCompleted(false);
        tasks.add(task);
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", tasks);
        return "tasks";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        model.addAttribute("taskModel", new TaskModel());
        return "task-form";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable("id") Long id, Model model) {
        model.addAttribute("taskModel", findTask(id));
        return "task-form";
    }

    @PostMapping("/task/save")
    public String saveTask(@ModelAttribute("taskModel") TaskModel form) {
        if (form.getId() == null) {
            form.setId(nextId++);
            tasks.add(form);
        } else {
            TaskModel task = findTask(form.getId());
            task.setName(form.getName());
            task.setDescription(form.getDescription());
            task.setDueDate(form.getDueDate());
            task.setCompleted(form.isCompleted());
        }

        return "redirect:/tasks";
    }

    private TaskModel findTask(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Задача не найдена"));
    }
}