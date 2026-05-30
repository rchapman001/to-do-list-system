package com.example.task_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task")
public class Task {

  @Id
  @Column(name = "task_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long taskId;

  @Column(name = "list_id")
  private Long listId;

  @Column(name = "task_name")
  private String taskName;

  @Column(name = "task_date")
  private LocalDate taskDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "task_status")
  private TaskStatus taskStatus;

  public Task(Long listId, String taskName, LocalDate taskDate, TaskStatus taskStatus) {
    this.listId = listId;
    this.taskName = taskName;
    this.taskDate = taskDate;
    this.taskStatus = taskStatus;
  }
}
