package com.assessment.messaging.consumer;

import com.assessment.messaging.dto.MessageDto;
import com.assessment.messaging.entity.Message;
import com.assessment.messaging.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    private final MessageRepository messageRepository;

    public MessageConsumer(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @RabbitListener(queues = "messageQueue")
    public void receiveMessage(String message) {
        try {
            MessageDto messageDTO = convertJsonToMessageDto(message);
            Message entityMessage = MessageDto.toMessage(messageDTO);
            messageRepository.save(entityMessage);
        } catch (Exception e) {
            logger.error("Error processing message: {}", message, e);
        }
    }

    private MessageDto convertJsonToMessageDto(String jsonMessage) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        return objectMapper.readValue(jsonMessage, MessageDto.class);
    }
}