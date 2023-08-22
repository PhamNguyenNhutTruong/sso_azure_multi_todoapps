package com.lab.todoapp2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lab.todoapp2.model.Todo;
import com.lab.todoapp2.repository.TodoRepository;

@Component
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public String addJob(String jobName) {
        try {
            UUID uuid = UUID.randomUUID();
            Todo todo = new Todo();

            todo.setId(uuid.toString());
            todo.setJobName(jobName);
            todoRepository.save(todo);
            return "Success";
        } catch (Exception e) {
            return "Fail";
        }
    }

    public List<Todo> getJobs() {
        List<Todo> listJobs = new ArrayList<>();
        listJobs = todoRepository.findAll();
        return listJobs;
    }
}
