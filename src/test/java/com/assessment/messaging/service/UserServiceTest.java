package com.assessment.messaging.service;

import com.assessment.messaging.entity.User;
import com.assessment.messaging.exception.ConflictException;
import com.assessment.messaging.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void insert_WithUniqueNickname_ShouldReturnSavedUser() {
        String nickname = "testuser";
        User user = new User();
        user.setNickName(nickname);

        when(userRepository.findByNickName(nickname)).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.insert(user);

        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository, times(1)).findByNickName(nickname);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void insert_WithNonUniqueNickname_ShouldThrowConflictException() {
        String nickname = "existing user";
        User user = new User();
        user.setNickName(nickname);

        when(userRepository.findByNickName(nickname)).thenReturn(user);

        ConflictException exception = assertThrows(ConflictException.class, () -> userService.insert(user));

        assertEquals(STR."Nickname is not unique: \{nickname}", exception.getMessage());
        verify(userRepository, times(1)).findByNickName(nickname);
        verifyNoMoreInteractions(userRepository);
    }
}
