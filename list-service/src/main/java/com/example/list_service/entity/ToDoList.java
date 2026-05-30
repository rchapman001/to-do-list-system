package com.example.list_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "to_do_list")
public class ToDoList {

  @Id
  @Column(name = "list_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long listId;

  @Column(name = "list_name")
  private String listName;

  public ToDoList(String listName) {
    this.listName = listName;
  }
}
