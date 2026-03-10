package uk.gov.hmcts.reform.dev.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public Task createTask(@Valid @RequestBody final Task task) {
//        System.out.println("title = " + task.getTitle());
//        System.out.println("description = " + task.getDescription());
//        System.out.println("status = " + task.getStatus());
//        System.out.println("dueDateTime = " + task.getDueDateTime());

        return taskRepository.save(task);
    }
}