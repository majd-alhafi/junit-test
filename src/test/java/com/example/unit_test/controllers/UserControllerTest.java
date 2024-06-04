package com.example.unit_test.controllers;

import com.example.unit_test.dto.UserRequest;
import com.example.unit_test.models.User;
import com.example.unit_test.repositories.UserRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import static com.example.unit_test.utils.Constants.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @BeforeEach
    public void beforeEach() {
        userRepository.deleteAll();
        mapper = new ObjectMapper();
        jsonParser = new JsonParser();
    }
    private ObjectMapper mapper;
    private JsonParser jsonParser;
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;


    static {
        mongoDBContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dymDynamicPropertyRegistry) {
        dymDynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    @Test
    @DisplayName("Create user with fully correct information")
    void createUserWithFullyCorrectInformation() throws Exception {
        UserRequest request = createSuccessfullyUserRequest();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
        assertEquals(1, userRepository.findAll().size());
    }
    @Test
    @DisplayName("Create user without email and username")
    void createUserWithoutUserNameAndEmailInformation() throws Exception {
        UserRequest request = createFailedUserRequest();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();

        JsonObject jsonObject = jsonParser.parse(responseJson).getAsJsonObject();
        String message = jsonObject.get("message").getAsString();
        assertEquals(message,MISSING_CREDENTIALS_EXCEPTION_MESSAGE);
        assertEquals(0, userRepository.findAll().size());
    }
    @Test
    @DisplayName("Create user when email is not unique")
    void createUserWhenEmailIsNotUnique() throws Exception {
        User user = User.builder()
                .id("123")
                .email("gg@gmail.com").build();
        userRepository.save(user);
        UserRequest request = createEmailNotUniqueUserRequest();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        JsonObject jsonObject = jsonParser.parse(responseJson).getAsJsonObject();
        String message = jsonObject.get("message").getAsString();
        assertEquals(message,EMAIL_ALREADY_EXISTS_EXCEPTION_MESSAGE);
    }
    @Test
    @DisplayName("Create user when username is not unique")
    void createUserWhenUserNameIsNotUnique() throws Exception {
        User user = User.builder()
                .id("123")
                .userName("gg@gmail.com").build();
        userRepository.save(user);
        UserRequest request = createUserNameNotUniqueUserRequest();
        MvcResult result= mockMvc.perform(MockMvcRequestBuilders.post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String responseJson = result.getResponse().getContentAsString();
        JsonObject jsonObject = jsonParser.parse(responseJson).getAsJsonObject();
        String message = jsonObject.get("message").getAsString();
        assertEquals(message,USERNAME_ALREADY_EXISTS_EXCEPTION_MESSAGE);
    }
    private UserRequest createSuccessfullyUserRequest(){
        return UserRequest.builder()
                .userName("Dummy")
                .email("Dummy@gmail.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("0587963587")
                .build();
    }
    private UserRequest createFailedUserRequest(){
        return UserRequest.builder()
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("0587963587")
                .build();
    }
    private UserRequest createEmailNotUniqueUserRequest(){
        return UserRequest.builder()
                .email("gg@gmail.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("0587963587")
                .build();
    }
    private UserRequest createUserNameNotUniqueUserRequest(){
        return UserRequest.builder()
                .userName("gg@gmail.com")
                .firstName("Dummy")
                .lastName("Dummy")
                .phoneNumber("0587963587")
                .build();
    }

}