package uk.gov.hmcts.reform.dev;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
class TasksFunctionalTest {
    protected static final String CONTENT_TYPE_VALUE = "application/json";

    @Value("${TEST_URL:http://localhost:8080}")
    private String testUrl;

    private String requestBody = """
            {
              "title": "Functional test task",
              "description": "Created by functional test",
              "status": "TODO",
              "dueDateTime": "2026-04-01T00:00:00"
            }
            """;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void whenGetTasks_thenReturns200() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void whenCreateTask_thenReturns201AndTaskBody() {

        Response response = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/tasks")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertEquals("Functional test task", response.jsonPath().getString("title"));
        Assertions.assertEquals("TODO", response.jsonPath().getString("status"));
    }

    @Test
    void whenUpdateTaskStatus_thenReturns200AndUpdatedTask() {
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/tasks")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(201, createResponse.statusCode());

        int taskId = createResponse.jsonPath().getInt("id");

        Response updateResponse = given()
            .contentType(ContentType.JSON)
            .queryParam("status", "DONE")
            .when()
            .post("/tasks/{id}/status", taskId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(200, updateResponse.statusCode());
        Assertions.assertEquals(taskId, updateResponse.jsonPath().getInt("id"));
        Assertions.assertEquals("DONE", updateResponse.jsonPath().getString("status"));
    }

    @Test
    void whenDeleteTask_thenReturns204AndTaskCannotBeFetched() {
        Response createResponse = given()
            .contentType(ContentType.JSON)
            .body(requestBody)
            .when()
            .post("/tasks")
            .then()
            .extract()
            .response();

        Assertions.assertEquals(201, createResponse.statusCode());

        int taskId = createResponse.jsonPath().getInt("id");

        Response deleteResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/tasks/{id}", taskId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(204, deleteResponse.statusCode());

        Response getResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks/{id}", taskId)
            .then()
            .extract()
            .response();

        Assertions.assertEquals(404, getResponse.statusCode());
    }

}
