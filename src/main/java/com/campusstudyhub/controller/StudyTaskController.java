package com.campusstudyhub.controller;

import com.campusstudyhub.entity.StudyTask;
import com.campusstudyhub.service.StudyTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for managing study tasks.
 */
@RestController
@RequestMapping("/api/v1/tasks")
public class StudyTaskController {

    private final StudyTaskService taskService;

    public StudyTaskController(StudyTaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<StudyTask> listTasks(Authentication auth) {
        return taskService.listUserTasks(auth.getName());
    }

    @PostMapping
    public StudyTask createTask(@RequestBody StudyTask task, Authentication auth) {
        return taskService.createTask(task, auth.getName());
    }

    @PatchMapping("/{id}/status")
    public StudyTask updateStatus(@PathVariable Long id, @RequestParam String status, Authentication auth) {
        return taskService.updateTaskStatus(id, status, auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication auth) {
        taskService.deleteTask(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
