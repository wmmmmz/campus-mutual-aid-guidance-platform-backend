package com.wmz.campusplatform.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "notify_announce")
@AllArgsConstructor
@NoArgsConstructor
public class NotifyAnnounce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "sender_id")
    private User sender;

    private String title;

    private String content;

    private Boolean isAuto;

    public Boolean getAuto() {
        return isAuto;
    }

    public void setAuto(Boolean auto) {
        isAuto = auto;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "notify_announce_receiver",
            joinColumns = @JoinColumn(name = "notify_announce_id"),
            inverseJoinColumns = @JoinColumn(name = "receiver_id"))
    private List<User> receiverList;

    @Override
    public String toString() {
        return "NotifyAnnounce{" +
                "id=" + id +
                ", sender=" + sender +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", isAuto=" + isAuto +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<User> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<User> receiverList) {
        this.receiverList = receiverList;
    }
}
