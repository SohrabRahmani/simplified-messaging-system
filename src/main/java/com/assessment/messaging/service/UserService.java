package com.assessment.messaging.service;

import com.assessment.messaging.dto.UserDTO;
import com.assessment.messaging.entity.User;
import com.assessment.messaging.exception.ConflictException;
import com.assessment.messaging.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO createUser(UserDTO userDto) {
        User user = UserDTO.toUser(userDto);

        if (!isNicknameUnique(user.getNickName())) {
            throw new ConflictException(STR."Nickname is not unique: \{user.getNickName()}");
        }
        return UserDTO.fromUser(userRepository.save(user));
    }

    private boolean isNicknameUnique(String nickname) {
        return userRepository.findByNickName(nickname) == null;
    }
}
