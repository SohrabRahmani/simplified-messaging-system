package com.assessment.messaging.controller;

import com.assessment.messaging.dto.MessageDto;
import com.assessment.messaging.entity.Message;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.repository.MessageRepository;
import com.assessment.messaging.repository.UserRepository;
import com.assessment.messaging.service.RabbitMQService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    @MockBean
    MessageRepository messageRepository;

    @MockBean
    RabbitMQService mqService;

    private List<Message> messageList = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        messageList = List.of(
                createMessage(1L,
                        createUser(1L, "Alex"),
                        createUser(2L, "Hana"),
                        "Hello World!",
                        LocalDateTime.now().withNano(0)),
                createMessage(2L,
                        createUser(1L, "Alex"),
                        createUser(2L, "Hana"),
                        "Hi!",
                        LocalDateTime.now().withNano(0)),
                createMessage(3L,
                        createUser(1L, "Alex"),
                        createUser(3L, "Moritz"),
                        "Hi ALex!",
                        LocalDateTime.now().withNano(0))
        );
    }

    @Test
    public void shouldCreateMessage() throws Exception {
        String requestBody = """
                {
                    "recipientId": 2,
                    "content": "Hello World!"
                }
                """;
        when(userRepository.findById(1L)).thenReturn(Optional.of(createUser(1L, "Alex")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(createUser(2L, "Hana")));
        when(messageRepository.save(any())).thenReturn(MessageDto.toMessage(new MessageDto(1L, 1L, 2L, "Hello World!", LocalDateTime.now())));
        doNothing().when(mqService).sendMessage(anyString(), any(MessageDto.class));

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("userId", 1))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenSenderAndRecipientIsSame() throws Exception {
        String requestBody = """
                {
                    "recipientId": 1,
                    "content": "Hello World!"
                }
                """;

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenSenderNotFound() throws Exception {
        String requestBody = """
                {
                    "recipientId": 1,
                    "content": "Hello World!"
                }
                """;

        when(messageRepository.findBySenderId(5L)).thenReturn(null);

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("userId", 5))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenRecipientNotFound() throws Exception {
        String requestBody = """
                {
                    "recipientId": 5,
                    "content": "Hello World!"
                }
                """;

        when(messageRepository.findByRecipientId(5L)).thenReturn(null);

        mockMvc.perform(post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("userId", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnAllMessagesThatReceived() throws Exception {
        String expectedJson = STR."""
                [
                    {
                        "id":\{messageList.get(0).getId()},
                        "senderId":\{messageList.get(0).getSender().getId()},
                        "recipientId":\{messageList.get(0).getRecipient().getId()},
                        "content":"\{messageList.get(0).getContent()}",
                        "timestamp":"\{messageList.get(0).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(1).getId()},
                        "senderId":\{messageList.get(1).getSender().getId()},
                        "recipientId":\{messageList.get(1).getRecipient().getId()},
                        "content":"\{messageList.get(1).getContent()}",
                        "timestamp":"\{messageList.get(1).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(2).getId()},
                        "senderId":\{messageList.get(2).getSender().getId()},
                        "recipientId":\{messageList.get(2).getRecipient().getId()},
                        "content":"\{messageList.get(2).getContent()}",
                        "timestamp":"\{messageList.get(2).getTimestamp()}"
                    }
                ]
                """;
        when(messageRepository.findByRecipientId(1L)).thenReturn(messageList);

        mockMvc.perform(get("/api/message/receive").header("userId", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void shouldReturnAllMessagesThatSent() throws Exception {
        String expectedJson = STR."""
                [
                    {
                        "id":\{messageList.get(0).getId()},
                        "senderId":1,
                        "recipientId":\{messageList.get(0).getRecipient().getId()},
                        "content":"\{messageList.get(0).getContent()}",
                        "timestamp":"\{messageList.get(0).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(1).getId()},
                        "senderId":1,
                        "recipientId":\{messageList.get(1).getRecipient().getId()},
                        "content":"\{messageList.get(1).getContent()}",
                        "timestamp":"\{messageList.get(1).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(2).getId()},
                        "senderId":1,
                        "recipientId":\{messageList.get(2).getRecipient().getId()},
                        "content":"\{messageList.get(2).getContent()}",
                        "timestamp":"\{messageList.get(2).getTimestamp()}"
                    }
                ]
                """;
        List<Message> updatedMessageList = List.copyOf(messageList);
        updatedMessageList.get(0).setSender(createUser(1L, "Alex"));
        updatedMessageList.get(1).setSender(createUser(1L, "Alex"));
        updatedMessageList.get(2).setSender(createUser(1L, "Alex"));

        when(messageRepository.findBySenderId(1L)).thenReturn(updatedMessageList);

        // When & Then
        mockMvc.perform(get("/api/message/send").header("userId", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    private User createUser(Long id, String nickName) {
        User user = new User();
        user.setId(id);
        user.setNickName(nickName);
        return user;
    }


    @Test
    public void shouldReturnAllMessagesThatReceivedFromParticularUser() throws Exception {
        String expectedJson = STR."""
                [
                    {
                        "id":\{messageList.get(0).getId()},
                        "senderId":2,
                        "recipientId":1,
                        "content":"\{messageList.get(0).getContent()}",
                        "timestamp":"\{messageList.get(0).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(1).getId()},
                        "senderId":2,
                        "recipientId":1,
                        "content":"\{messageList.get(1).getContent()}",
                        "timestamp":"\{messageList.get(1).getTimestamp()}"
                    },
                    {
                        "id":\{messageList.get(2).getId()},
                        "senderId":2,
                        "recipientId":1,
                        "content":"\{messageList.get(2).getContent()}",
                        "timestamp":"\{messageList.get(2).getTimestamp()}"
                    }
                ]
                """;

        List<Message> updatedMessageList = List.copyOf(messageList);
        updatedMessageList.get(0).setRecipient(createUser(1L, "Alex"));
        updatedMessageList.get(1).setRecipient(createUser(1L, "Alex"));
        updatedMessageList.get(2).setRecipient(createUser(1L, "Alex"));

        updatedMessageList.get(0).setSender(createUser(2L, "Hana"));
        updatedMessageList.get(1).setSender(createUser(2L, "Hana"));
        updatedMessageList.get(2).setSender(createUser(2L, "Hana"));

        when(messageRepository.findByRecipientIdAndSenderId(1L, 2L)).thenReturn(updatedMessageList);

        // When & Then
        mockMvc.perform(get("/api/message/receive/2").header("userId", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedJson));
    }

    private Message createMessage(Long id, User recipient, User sender, String content, LocalDateTime dateTime) {
        Message message = new Message();
        message.setId(id);
        message.setRecipient(recipient);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(dateTime);
        return message;
    }
}
