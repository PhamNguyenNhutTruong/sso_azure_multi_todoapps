package com.lab.todoapp1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lab.todoapp1.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String>{
    
}
