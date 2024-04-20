package com.assessment.messaging.controller;

import com.assessment.messaging.dto.UserDTO;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.exception.ConflictException;
import com.assessment.messaging.repository.UserRepository;
import com.assessment.messaging.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @Test
    public void shouldCreateUser() throws Exception {
        String requestBody = """
            {
                "nickName":"Alex"
            }
            """;

        String expectedResponseBody = """
            {
                "Id":1,
                "nickName":"Alex"
            }
            """;

        User user = new User();
        user.setId(1L);
        user.setNickName("Alex");

        when(userRepository.findByNickName(any())).thenReturn(null); // Assuming no user exists with the given nickname
        when(userRepository.save(any())).thenReturn(user);

        mockMvc.perform(post("/api/user").contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(content().json(expectedResponseBody));
    }

    @Test
    public void shouldReturnConflictWhenCreatingUserWithNonUniqueNickname() throws Exception {
        String body = """
                 {
                    "nickName":"Alex"
                 }
                """;
        when(userRepository.findByNickName("Alex")).thenReturn(createUser(1L,"Alex"));

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict());
    }

    private User createUser(Long id, String nickName) {
        User user = new User();
        user.setId(id);
        user.setNickName(nickName);
        return user;
    }
}
