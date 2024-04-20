package com.assessment.messaging.service;

import com.assessment.messaging.dto.MessageDTO;
import com.assessment.messaging.entity.Message;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.exception.IllegalArgumentException;
import com.assessment.messaging.exception.NotFoundException;
import com.assessment.messaging.repository.MessageRepository;
import com.assessment.messaging.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RabbitMQService mqService;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, RabbitMQService mqService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.mqService = mqService;
    }

    public Message sendMessage(Long senderId, MessageDTO messageDTO) {
        validateSenderAndRecipient(senderId, messageDTO.recipientId());

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException(STR."Sender user not found with ID: \{senderId}"));

        User recipient = userRepository.findById(messageDTO.recipientId())
                .orElseThrow(() -> new NotFoundException("Recipient user not found with ID: " + messageDTO.recipientId()));

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(messageDTO.content());
        message.setTimestamp(LocalDateTime.now());

        message = messageRepository.save(message);

        mqService.sendMessage("messageQueue", toJson(MessageDTO.fromMessage(message)));
        return message;
    }

    public List<Message> getAllReceiveMessage(Long recipientId) {
        return messageRepository.findByRecipientId(recipientId);
    }

    public List<Message> getAllSendMessage(Long senderId) {
        return messageRepository.findBySenderId(senderId);
    }

    public List<Message> getAllReceiveMessageFromParticularUser(Long recipientId, Long senderId) {
        return messageRepository.findByRecipientIdAndSenderId(recipientId, senderId);
    }

    private void validateSenderAndRecipient(Long senderId, Long recipientId) {
        if (senderId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot send a message to yourself.");
        }
    }

    private String toJson(MessageDTO messageDTO) {
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        try {
            return objectMapper.writeValueAsString(messageDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting message to JSON", e);
        }
    }
}
