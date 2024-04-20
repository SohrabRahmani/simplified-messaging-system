package com.assessment.messaging.dto;

import com.assessment.messaging.entity.User;

public record UserDTO(Long Id, String nickName) {
    public static UserDTO fromUser(User user) {
        return new UserDTO(user.getId(), user.getNickName());
    }

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        user.setId(userDTO.Id);
        user.setNickName(userDTO.nickName);
        return user;
    }
}
