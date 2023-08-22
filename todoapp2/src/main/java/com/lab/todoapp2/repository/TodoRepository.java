package com.lab.todoapp2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lab.todoapp2.model.Todo;

@Repository
public interface TodoRepository extends JpaRepository<Todo, String>{
    
}
