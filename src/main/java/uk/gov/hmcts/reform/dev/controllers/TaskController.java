package uk.gov.hmcts.reform.dev.controllers;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(final TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody final Task task) {
        Task savedTask = taskRepository.save(task);

        return ResponseEntity
            .created(URI.create("/tasks/" + savedTask.getId()))
            .body(savedTask);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable final Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id " + id
            ));

        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable final Long id,
                                                 @RequestParam("status") final TaskStatus status) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id " + id
            ));

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);

        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable final Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id " + id
            ));

        taskRepository.delete(task);

        return ResponseEntity.noContent().build();
    }
}
