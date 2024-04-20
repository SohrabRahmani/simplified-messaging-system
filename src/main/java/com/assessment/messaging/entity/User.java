package com.assessment.messaging.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String nickName;

    @OneToMany(mappedBy = "sender")
    private Set<Message> messagesSent;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Set<Message> getMessagesSent() {
        return messagesSent;
    }

    public void setMessagesSent(Set<Message> messagesSent) {
        this.messagesSent = messagesSent;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id=" + Id +
                ", nickName='" + nickName + '\'' +
                ", messagesSent=" + messagesSent +
                '}';
    }
}
