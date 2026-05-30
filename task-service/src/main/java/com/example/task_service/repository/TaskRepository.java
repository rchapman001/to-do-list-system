package com.example.task_service.repository;

import com.example.task_service.entity.Task;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  List<Task> findTasksByListId(Long id); // Method to find tasks by list ID
}
