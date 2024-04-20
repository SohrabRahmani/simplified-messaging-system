package com.assessment.messaging.controller;

import com.assessment.messaging.dto.UserDTO;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Users", description = "APIs for managing users")
@Controller
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a user", description = "Create a new user with the provided details.")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "User created successfully")})
    @PostMapping(value = "/user")
    public ResponseEntity<UserDTO> createUser(@RequestBody final UserDTO userDto) {
        User user = UserDTO.toUser(userDto);
        User createdUser = userService.insert(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserDTO.fromUser(createdUser));
    }
}
