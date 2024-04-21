package com.assessment.messaging.dto;

import com.assessment.messaging.entity.Message;
import com.assessment.messaging.entity.User;

import java.time.LocalDateTime;

public record MessageDto(Long id, Long senderId, Long recipientId, String content, LocalDateTime timestamp) {
    public static MessageDto fromMessage(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getContent(),
                message.getTimestamp()
        );
    }

    public static Message toMessage(MessageDto messageDTO) {
        Message message = new Message();
        message.setId(messageDTO.id());

        User sender = new User();
        sender.setId(messageDTO.senderId());
        message.setSender(sender);

        User recipient = new User();
        recipient.setId(messageDTO.recipientId());
        message.setRecipient(recipient);

        message.setContent(messageDTO.content());
        message.setTimestamp(messageDTO.timestamp());

        return message;
    }
}
