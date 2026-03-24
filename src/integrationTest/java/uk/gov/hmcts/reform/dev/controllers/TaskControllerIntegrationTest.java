package uk.gov.hmcts.reform.dev.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    private Task newTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        newTask = new Task(
            null,
            "Title",
            "Description",
            TaskStatus.TODO,
            LocalDateTime.of(2026, 1, 1, 0, 0)
        );
    }

    @Test
    void givenTasks_whenGetTasks_thenReturnOK() throws Exception {
        taskRepository.save(newTask);

        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Title"))
            .andExpect(jsonPath("$[0].description").value("Description"));
    }

    @Test
    void givenTasks_whenNoTasks_thenReturnEmptyList() throws Exception {
        mockMvc.perform(get("/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void givenTaskExists_whenGetTaskID_thenReturnTask() throws Exception {
        Task savedTask = taskRepository.save(newTask);

        mockMvc.perform(get("/tasks/{id}", savedTask.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(savedTask.getId()))
            .andExpect(jsonPath("$.title").value("Title"))
            .andExpect(jsonPath("$.description").value("Description"));
    }

    @Test
    void givenTaskDoesntExist_whenGetTaskID_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void givenRequestOK_whenCreateTask_thenReturnTask() throws Exception {
        mockMvc.perform(post("/tasks")
                            .contentType("application/json")
                            .content("""
                    {
                      "title": "Title",
                      "description": "Description",
                      "status": "TODO",
                      "dueDateTime": "2026-01-01T00:00:00"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Title"))
            .andExpect(jsonPath("$.description").value("Description"))
            .andExpect(jsonPath("$.status").value("TODO"))
            .andExpect(jsonPath("$.dueDateTime").value("2026-01-01T00:00:00"));
    }

    @Test
    void givenRequestInvalid_whenCreateTask_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/tasks")
                            .contentType("application/json")
                            .content("""
                    {
                      "title": "",
                      "description": "Description",
                      "status": "TODO",
                      "dueDateTime": "2026-01-01T00:00:00"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenTaskExists_whenDeleteTask_thenReturnOK() throws Exception {
        Task savedTask = taskRepository.save(newTask);

        mockMvc.perform(post("/tasks/{id}/delete", savedTask.getId()))
            .andExpect(status().isOk());
    }

    @Test
    void givenTaskExists_whenDeleteTask_thenTaskIsRemoved() throws Exception {
        Task savedTask = taskRepository.save(newTask);

        mockMvc.perform(post("/tasks/{id}/delete", savedTask.getId()))
            .andExpect(status().isOk());

        assert(taskRepository.findById(savedTask.getId()).isEmpty());
    }

    @Test
    void givenTaskExists_whenUpdateStatus_thenReturnOK() throws Exception {
        Task savedTask = taskRepository.save(newTask);

        mockMvc.perform(post("/tasks/{id}/status", savedTask.getId())
                            .param("status", "DONE"))
            .andExpect(status().isOk());
    }

    @Test
    void givenTaskExists_whenUpdateStatus_thenStatusIsUpdatedInDatabase() throws Exception {
        Task savedTask = taskRepository.save(newTask);

        mockMvc.perform(post("/tasks/{id}/status", savedTask.getId())
                            .param("status", "DONE"))
            .andExpect(status().isOk());

        Task updatedTask = taskRepository.findById(savedTask.getId()).orElseThrow();

        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }
}
