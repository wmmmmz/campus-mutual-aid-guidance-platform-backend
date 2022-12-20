package com.wmz.campusplatform.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Conversation conversation;

    private String content;

    private Date publishTime;

    private Boolean isImg;

    public Message() {
    }

    public Message(Integer id, User user, Conversation conversation, String content, Date publishTime, Boolean isImg) {
        this.id = id;
        this.user = user;
        this.conversation = conversation;
        this.content = content;
        this.publishTime = publishTime;
        this.isImg = isImg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getImg() {
        return isImg;
    }

    public void setImg(Boolean img) {
        isImg = img;
    }


}
