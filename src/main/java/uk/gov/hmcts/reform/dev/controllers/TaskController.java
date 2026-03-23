package uk.gov.hmcts.reform.dev.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
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
    public Task createTask(@Valid @RequestBody final Task task) {
        return taskRepository.save(task);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id " + id
            ));
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    ;

    @PostMapping("/{id}/status")
    public String updateTaskStatus(@PathVariable("id") Long id,
                                   @RequestParam("status") TaskStatus status) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found with id " + id
            ));

        task.setStatus(status);
        taskRepository.save(task);

        return "redirect:/tasks";
    }

    @PostMapping("/{id}/delete")
    public String deleteTaskByID(@PathVariable("id") Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }

}
