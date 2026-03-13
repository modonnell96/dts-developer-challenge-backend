package uk.gov.hmcts.reform.dev.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.UpdateTaskStatusRequest;
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
	public Task getTaskById(@PathVariable("id") Long id) {
		return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
	}

	@GetMapping
	public List<Task> getAllTasks() {
		return taskRepository.findAll();
	};

	@PatchMapping("/{id}/status")
	public Task updateTaskStatus(@PathVariable("id") Long id, @Valid @RequestBody UpdateTaskStatusRequest request) {
		Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found with id " + id));

		task.setStatus(request.getStatus());

		return taskRepository.save(task);
	};

	@DeleteMapping("/{id}")
	public void deleteTaskByID(@PathVariable("id") Long id) {
		taskRepository.deleteById(id);
	}

}