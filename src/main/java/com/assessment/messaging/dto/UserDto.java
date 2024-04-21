package com.assessment.messaging.dto;

import com.assessment.messaging.entity.User;

public record UserDto(Long Id, String nickName) {
    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(), user.getNickName());
    }

    public static User toUser(UserDto userDTO) {
        User user = new User();
        user.setId(userDTO.Id);
        user.setNickName(userDTO.nickName);
        return user;
    }
}
