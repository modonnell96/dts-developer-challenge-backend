package uk.gov.hmcts.reform.dev.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.models.TaskStatus;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@WebMvcTest(TaskController.class)
class TaskControllerIntegrationTest {


    @Autowired
    private transient MockMvc mockMvc;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    void givenTasks_whenGetTasks_thenReturnOK() throws Exception {
        Task newTask = new Task(1L, "Title", "Description", TaskStatus.TODO, LocalDateTime.now());
        when(taskRepository.findAll()).thenReturn(List.of(newTask));

        mockMvc.perform(get("/tasks"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value("Description"));
    }

    @Test
    void givenTasks_whenNoTasks_thenReturnEmptyList() throws Exception {
        when(taskRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/tasks"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void givenTaskExists_whenGetTaskID_thenReturnTask() throws Exception {
        Task newTask = new Task(1L, "Title", "Description", TaskStatus.TODO, LocalDateTime.now());
        when(taskRepository.findById(any(long.class))).thenReturn(Optional.of(newTask));

        mockMvc.perform(get("/tasks/1"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Title"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Description"));


    }

    @Test
    void givenTaskDoesntExist_whenGetTaskID_thenReturnNotFound() throws Exception {
        Task newTask = new Task(1L, "Title", "Description", TaskStatus.TODO, LocalDateTime.now());
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/2"))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void givenRequestOK_whenCreateTask_thenReturnTask() throws Exception {
        Task newTask = new Task(1L, "Title", "Description", TaskStatus.TODO, LocalDateTime.of(2026, 01, 01, 00, 00));

        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        mockMvc.perform(post("/tasks")
                            .contentType("application/json")
                            .content("""
                                         {
                                           "id": 1,
                                           "title": "Title",
                                           "description": "Description",
                                           "status": "TODO",
                                           "dueDateTime": "2026-01-01T00:00:00"                }
                                         """))
            .andExpect(status().isOk());
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
                                               "dueDateTime": "2026-01-01T00:00:00"}
                                         """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void givenTaskExists_whenDeleteTask_thenReturnOK() throws Exception {
        when(taskRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/tasks/1/delete"))
            .andExpect(status().isOk());
    }
    @Test
    void givenTaskExists_whenUpdateStatus_thenReturnOK() throws Exception {
        Task newTask = new Task(1L, "Title", "Description", TaskStatus.TODO, LocalDateTime.of(2026, 01, 01, 00, 00));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(newTask));

        mockMvc.perform(post("/tasks/1/status")
                            .param("status", "DONE"))
            .andExpect(status().isOk());
    }
}
