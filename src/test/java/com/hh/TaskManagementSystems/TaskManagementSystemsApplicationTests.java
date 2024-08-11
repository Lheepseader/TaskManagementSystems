package com.hh.TaskManagementSystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hh.TaskManagementSystems.dto.AuthRequestDto;
import com.hh.TaskManagementSystems.dto.CommentDto;
import com.hh.TaskManagementSystems.dto.RegistrationRequestDto;
import com.hh.TaskManagementSystems.dto.TaskDto;
import com.hh.TaskManagementSystems.service.UserService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TaskManagementSystemsApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void testCreateUser() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto("testuser1@example.com", "testpassword");

        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Request body: " + jsonRequest);
        System.out.println("Response body: " + jsonResponse);
    }

    @Test
    void testCreateTask() throws Exception {
        RegistrationRequestDto registrationRequest = new RegistrationRequestDto("testuser2@example.com",
                "testpassword");

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        AuthRequestDto loginRequest = new AuthRequestDto("testuser2@example.com", "testpassword");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        String jwtToken = JsonPath.parse(loginResponse).read("$.jwt");

        TaskDto taskDto = TaskDto.builder()
                .title("New Title")
                .description("New Description")
                .build();

        String jsonRequest = objectMapper.writeValueAsString(taskDto);

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();


        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Request body: " + jsonRequest);
        System.out.println("Response body: " + jsonResponse);
    }

    @Test
    void testAddComment() throws Exception {
        RegistrationRequestDto registrationRequest = new RegistrationRequestDto("testuser3@example.com",
                "testpassword");

        mockMvc.perform(post("/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isOk());

        AuthRequestDto loginRequest = new AuthRequestDto("testuser3@example.com", "testpassword");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();


        String loginResponse = loginResult.getResponse().getContentAsString();
        String jwtToken = JsonPath.parse(loginResponse).read("$.jwt");

        TaskDto taskDto = TaskDto.builder()
                .title("New Title")
                .description("New Description")
                .build();

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andReturn();
        String response = result.getResponse().getContentAsString();
        System.out.println(result);
        System.out.println(response);
        result = mockMvc.perform(get("/api/tasks/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();
        response = result.getResponse().getContentAsString();
        System.out.println(response);
        System.out.println(result);


        Long taskId = JsonPath.parse(result.getResponse().getContentAsString())
                .read("$[0].id", Long.class);
        System.out.println(response);
        CommentDto commentDto = CommentDto.builder()
                .body("Комментарий новый")
                .taskId(taskId)
                .build();

        result = mockMvc.perform(post("/api/tasks/" + taskId + "/comments")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println(response);
        System.out.println(result);
    }

}
