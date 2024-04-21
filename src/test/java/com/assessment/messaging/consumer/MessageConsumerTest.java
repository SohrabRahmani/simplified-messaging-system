package com.assessment.messaging.consumer;

import com.assessment.messaging.entity.Message;
import com.assessment.messaging.repository.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageConsumer messageConsumer;

    @Test
    void receiveMessage_Success() {
        String jsonMessage = """
                {"id":1,"senderId":1,"recipientId":2,"content":"Hello","timestamp":"2024-04-21T12:00:00"}
                """;

        messageConsumer.receiveMessage(jsonMessage);

        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void receiveMessage_InvalidMessage() {
        String invalidJsonMessage = "Invalid JSON message";

        messageConsumer.receiveMessage(invalidJsonMessage);

        verifyNoInteractions(messageRepository);
    }
}
