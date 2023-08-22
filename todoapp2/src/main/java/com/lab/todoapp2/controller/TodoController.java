package com.lab.todoapp2.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.lab.todoapp2.model.Todo;
import com.lab.todoapp2.service.TodoService;

@RestController
@CrossOrigin
public class TodoController {
    @Autowired
    private TodoService todoService;

    @GetMapping("/add-job/{jobName}")
    @PreAuthorize("hasAuthority('APPROLE_Admin')")
    public String addJob(@PathVariable("jobName") String jobName) {
        String result = todoService.addJob(jobName);
        return "Add job to todo app2 : " + result;
    }

    @GetMapping("/get-jobs")
    @PreAuthorize("hasAuthority('APPROLE_Admin')")
    public List<Todo> getJobs() {
        List<Todo> listJobs = new ArrayList<>();
        listJobs = todoService.getJobs();
        return listJobs;
    }
}