package com.wmz.campusplatform.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String avatarUrl;

    private String name;

    @JsonIgnore
    @JsonIgnoreProperties("conversationList")
    @JoinTable(name = "user_conversation",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @ManyToMany
    private List<User> userList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Conversation() {
    }

    public Conversation(Integer id, String avatarUrl, String name) {
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.name = name;
    }
}
