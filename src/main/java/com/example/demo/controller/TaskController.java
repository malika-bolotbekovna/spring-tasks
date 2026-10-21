package com.example.demo.controller;

import com.example.demo.model.TaskModel;
import com.example.demo.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/tasks")
    public String listTasks(Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks";
    }

    @GetMapping("/task/new")
    public String newTask(Model model) {
        model.addAttribute("taskModel", new TaskModel());
        return "task-form";
    }

    @GetMapping("/task/edit/{id}")
    public String editTask(@PathVariable("id") Long id, Model model) {
        model.addAttribute("taskModel", taskService.findById(id));
        return "task-form";
    }

    @PostMapping("/task/save")
    public String saveTask(
            @Valid @ModelAttribute("taskModel") TaskModel taskModel,
            BindingResult result) {

        if (result.hasErrors()) {
            return "task-form";
        }

        taskService.save(taskModel);
        return "redirect:/tasks";
    }

    @PostMapping("/task/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id) {
        taskService.deleteById(id);
        return "redirect:/tasks";
    }

    @GetMapping("/tasks/filter")
    public String filterTasks(
            @RequestParam(name = "completed", required = false)
            Boolean completed,

            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            Model model) {

        model.addAttribute("completed", completed);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        String filterError = null;

        if ((startDate == null) != (endDate == null)) {
            filterError = "Для диапазона укажите обе даты.";
        } else if (startDate != null && startDate.isAfter(endDate)) {
            filterError = "Начальная дата не должна быть позже конечной.";
        }

        if (filterError != null) {
            model.addAttribute("filterError", filterError);
            model.addAttribute("tasks", List.of());
            return "tasks";
        }

        model.addAttribute("tasks",
                taskService.filter(completed, startDate, endDate));

        return "tasks";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNotFound(NoSuchElementException exception) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, exception.getMessage(), exception);
    }
}