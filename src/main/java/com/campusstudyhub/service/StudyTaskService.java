package com.campusstudyhub.service;

import com.campusstudyhub.entity.StudyTask;
import com.campusstudyhub.entity.User;
import com.campusstudyhub.repository.StudyTaskRepository;
import com.campusstudyhub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing study tasks.
 */
@Service
public class StudyTaskService {

    private final StudyTaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    public StudyTaskService(StudyTaskRepository taskRepository, UserRepository userRepository,
            AnalyticsService analyticsService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.analyticsService = analyticsService;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public List<StudyTask> listUserTasks(String email) {
        User user = getUser(email);
        return taskRepository.findByUserIdOrderByDueDateAsc(user.getId());
    }

    @Transactional(readOnly = true)
    public List<StudyTask> listActiveTasks(String email) {
        User user = getUser(email);
        return taskRepository.findByUserIdAndStatusOrderByDueDateAsc(user.getId(), "TODO");
    }

    @Transactional
    public StudyTask createTask(StudyTask task, String email) {
        User user = getUser(email);
        task.setUserId(user.getId());
        StudyTask saved = taskRepository.save(task);

        analyticsService.trackEvent("study_task_create",
                java.util.Map.of("taskId", saved.getId(), "title", saved.getTitle()),
                email);

        return saved;
    }

    @Transactional
    public StudyTask updateTaskStatus(Long taskId, String status, String email) {
        User user = getUser(email);
        StudyTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (!task.getUserId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized: You do not own this task.");
        }

        task.setStatus(status);
        StudyTask saved = taskRepository.save(task);

        analyticsService.trackEvent("study_task_status_change",
                java.util.Map.of("taskId", taskId, "newStatus", status),
                email);

        return saved;
    }

    @Transactional
    public void deleteTask(Long taskId, String email) {
        User user = getUser(email);
        StudyTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (!task.getUserId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized: You do not own this task.");
        }

        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public long countPendingTasks(String email) {
        User user = getUser(email);
        return taskRepository.countByUserIdAndStatus(user.getId(), "TODO") +
                taskRepository.countByUserIdAndStatus(user.getId(), "IN_PROGRESS");
    }
}
