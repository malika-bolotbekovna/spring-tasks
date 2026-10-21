package com.example.demo.repository;

import com.example.demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed);

    List<Task> findByDueDateBetween(
            LocalDate startDate, LocalDate endDate);

    List<Task> findByCompletedAndDueDateBetween(
            boolean completed, LocalDate startDate, LocalDate endDate);
}