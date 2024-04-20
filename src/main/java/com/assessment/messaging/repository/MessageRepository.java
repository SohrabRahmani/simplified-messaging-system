package com.assessment.messaging.repository;

import com.assessment.messaging.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByRecipientId(Long id);

    List<Message> findBySenderId(Long id);

    List<Message> findByRecipientIdAndSenderId(Long recipientId, Long senderId);
}
