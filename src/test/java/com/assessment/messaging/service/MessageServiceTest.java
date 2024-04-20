package com.assessment.messaging.service;

import com.assessment.messaging.dto.MessageDTO;
import com.assessment.messaging.entity.Message;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.exception.IllegalArgumentException;
import com.assessment.messaging.exception.NotFoundException;
import com.assessment.messaging.repository.MessageRepository;
import com.assessment.messaging.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitMQService mqService;

    @InjectMocks
    private MessageService messageService;

    @Test
    void sendMessage_WithValidInputs_ShouldReturnSavedMessage() {
        Long senderId = 1L;
        Long recipientId = 2L;
        String content = "Hello World!";
        LocalDateTime timestamp = LocalDateTime.now();

        User sender = new User();
        sender.setId(senderId);

        User recipient = new User();
        recipient.setId(recipientId);

        MessageDTO messageDTO = new MessageDTO(null, senderId, recipientId, content, null);

        Message savedMessage = new Message();
        savedMessage.setId(1L);
        savedMessage.setSender(sender);
        savedMessage.setRecipient(recipient);
        savedMessage.setContent(content);
        savedMessage.setTimestamp(timestamp);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        Message result = messageService.sendMessage(senderId, messageDTO);

        assertNotNull(result);
        assertEquals(savedMessage, result);
        assertEquals(sender, result.getSender());
        assertEquals(recipient, result.getRecipient());
        assertEquals(content, result.getContent());
        assertNotNull(result.getTimestamp());
        verify(mqService, times(1)).sendMessage(anyString(), anyString());
    }

    @Test
    void sendMessage_WithSameSenderIdAndRecipientId_ShouldThrowIllegalArgumentException() {
        Long senderId = 1L;
        String content = "Hello World!";

        MessageDTO messageDTO = new MessageDTO(null, senderId, senderId, content, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(senderId, messageDTO));

        assertEquals("Cannot send a message to yourself.", exception.getMessage());
        verifyNoInteractions(userRepository, messageRepository, mqService);
    }

    @Test
    void sendMessage_WithNonExistingSender_ShouldThrowNotFoundException() {
        Long senderId = 1L;
        Long recipientId = 2L;
        String content = "Hello World!";

        MessageDTO messageDTO = new MessageDTO(null, senderId, recipientId, content, null);

        when(userRepository.findById(senderId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> messageService.sendMessage(senderId, messageDTO));

        assertEquals(STR."Sender user not found with ID: \{senderId}", exception.getMessage());
        verify(userRepository, times(1)).findById(senderId);
        verifyNoMoreInteractions(userRepository, messageRepository, mqService);
    }

    @Test
    void sendMessage_WithNonExistingRecipient_ShouldThrowNotFoundException() {
        Long senderId = 1L;
        Long recipientId = 2L;
        String content = "Hello World!";

        User sender = new User();
        sender.setId(senderId);

        MessageDTO messageDTO = new MessageDTO(null, senderId, recipientId, content, null);

        when(userRepository.findById(senderId)).thenReturn(Optional.of(sender));
        when(userRepository.findById(recipientId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> messageService.sendMessage(senderId, messageDTO));

        assertEquals(STR."Recipient user not found with ID: \{recipientId}", exception.getMessage());
        verify(userRepository, times(1)).findById(recipientId);
        verify(userRepository, times(1)).findById(senderId);
        verifyNoMoreInteractions(userRepository, messageRepository, mqService);
    }
}
