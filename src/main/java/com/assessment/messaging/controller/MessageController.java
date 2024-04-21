package com.assessment.messaging.controller;

import com.assessment.messaging.dto.MessageDto;
import com.assessment.messaging.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Messages", description = "APIs for managing messages")
@Controller
@RequestMapping(value = "/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(final MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(summary = "Send a message", description = "Send a message to a user and store it in the database. Also, put it into a messaging queue.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Message sent successfully")})
    @PostMapping(value = "/message")
    public ResponseEntity<MessageDto> createMessage(
            @RequestBody MessageDto messageDto,
            @RequestHeader("userId") Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(userId, messageDto));
    }

    @Operation(summary = "Get received messages", description = "Retrieve all messages received by the authenticated user.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of received messages")})
    @GetMapping(value = "/message/receive")
    public ResponseEntity<List<MessageDto>> listReceivedMessages(@RequestHeader Long userId) {
        return ResponseEntity.ok(messageService.getAllReceiveMessage(userId));
    }

    @Operation(summary = "Get messages received from a user", description = "Retrieve all messages received from a particular user.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of received messages from the specified user")})
    @GetMapping(value = "/message/receive/{senderId}")
    public ResponseEntity<List<MessageDto>> listReceivedMessagesFromParticularUser(
            @RequestHeader Long userId,
            @PathVariable Long senderId
    ) {
        return ResponseEntity.ok(messageService.getAllReceiveMessageFromParticularUser(userId, senderId));
    }

    @Operation(summary = "Get sent messages", description = "Retrieve all messages sent by the authenticated user.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of sent messages")})
    @GetMapping(value = "/message/send")
    public ResponseEntity<List<MessageDto>> listSentMessages(@RequestHeader Long userId) {
        return ResponseEntity.ok(messageService.getAllSendMessage(userId));
    }
}
