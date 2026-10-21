package com.example.demo.service;

import com.example.demo.entity.Task;
import com.example.demo.model.TaskModel;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskModel> findAll() {
        return taskRepository.findAll().stream()
                .map(this::convertToModel)
                .toList();
    }

    public TaskModel findById(Long id) {
        return convertToModel(getTask(id));
    }

    public List<TaskModel> findByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed).stream()
                .map(this::convertToModel)
                .toList();
    }

    @Transactional
    public void save(TaskModel model) {
        Task task = model.getId() == null
                ? new Task()
                : getTask(model.getId());

        task.setName(model.getName());
        task.setDescription(model.getDescription());
        task.setDueDate(model.getDueDate());
        task.setCompleted(model.isCompleted());

        taskRepository.save(task);
    }

    @Transactional
    public void deleteById(Long id) {
        taskRepository.delete(getTask(id));
    }

    private Task getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Задача не найдена: " + id));
    }

    private TaskModel convertToModel(Task task) {
        TaskModel model = new TaskModel();
        model.setId(task.getId());
        model.setName(task.getName());
        model.setDescription(task.getDescription());
        model.setDueDate(task.getDueDate());
        model.setCompleted(task.isCompleted());
        return model;
    }

    public List<TaskModel> filter(
            Boolean completed,
            LocalDate startDate,
            LocalDate endDate) {

        List<Task> tasks;

        if (startDate != null && endDate != null) {
            if (completed == null) {
                tasks = taskRepository.findByDueDateBetween(
                        startDate, endDate);
            } else {
                tasks = taskRepository.findByCompletedAndDueDateBetween(
                        completed, startDate, endDate);
            }
        } else if (completed != null) {
            tasks = taskRepository.findByCompleted(completed);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(this::convertToModel)
                .toList();
    }
}