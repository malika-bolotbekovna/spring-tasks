package com.example.demo;

import com.example.demo.entity.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("db-check")
public class DatabaseCheck implements CommandLineRunner {

    private final TaskRepository taskRepository;

    public DatabaseCheck(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) {
        Task task = new Task();
        task.setName("Тест JPA");
        task.setDescription("Добавление и чтение задачи через репозиторий");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCompleted(false);

        Task saved = taskRepository.save(task);

        Task loaded = taskRepository.findById(saved.getId())
                .orElseThrow(() ->
                        new IllegalStateException("Задача не найдена в БД"));

        if (!"Тест JPA".equals(loaded.getName())) {
            throw new IllegalStateException("Данные задачи не совпадают");
        }

        System.out.println("ПРОВЕРКА JPA УСПЕШНА: id="
                + loaded.getId() + ", name=" + loaded.getName());

        System.out.println("Всего задач в БД: " + taskRepository.count());
    }
}