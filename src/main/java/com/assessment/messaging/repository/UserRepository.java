package com.assessment.messaging.repository;

import com.assessment.messaging.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByNickName(String nickName);
}
