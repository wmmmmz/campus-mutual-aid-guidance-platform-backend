package com.wmz.campusplatform.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String stuId;

    private String pwd;

    private String name;

    private String className;

    private String tel;

    private String wx;

    private String role;

    private String description;

    private String interviewStatus;

    private String imgUrl;

    private Boolean isLocked;

    //用户收到的通知列表
    @JsonIgnoreProperties(value = "receiverList")
    @ManyToMany(mappedBy = "receiverList")
    private List<NotifyAnnounce> notifyAnnounceList;

    //用户报名上课的class列表
//    @JsonIgnore
//    @JsonIgnoreProperties(value = "studentList")
    @ManyToMany(mappedBy = "studentList")
    private List<Class> classList;

    //用户曾报名授课的class列表
    @JsonIgnore
    @JsonIgnoreProperties(value = "interviewList")
    @ManyToMany(mappedBy = "interviewList")
    private List<Class> teachEnrollClassList;

    public User() {
    }

    public User(Integer id, String stuId, String pwd, String name, String className, String tel, String wx, String role, String description, String interviewStatus, String imgUrl) {
        this.id = id;
        this.stuId = stuId;
        this.pwd = pwd;
        this.name = name;
        this.className = className;
        this.tel = tel;
        this.wx = wx;
        this.role = role;
        this.description = description;
        this.interviewStatus = interviewStatus;
        this.imgUrl = imgUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWx() {
        return wx;
    }

    public void setWx(String wx) {
        this.wx = wx;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public void setInterviewStatus(String interviewStatus) {
        this.interviewStatus = interviewStatus;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<NotifyAnnounce> getNotifyAnnounceList() {
        return notifyAnnounceList;
    }

    public void setNotifyAnnounceList(List<NotifyAnnounce> notifyAnnounceList) {
        this.notifyAnnounceList = notifyAnnounceList;
    }

    public List<Class> getClassList() {
        return classList;
    }

    public void setClassList(List<Class> classList) {
        this.classList = classList;
    }

    public List<Class> getTeachEnrollClassList() {
        return teachEnrollClassList;
    }

    public void setTeachEnrollClassList(List<Class> teachEnrollClassList) {
        this.teachEnrollClassList = teachEnrollClassList;
    }

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }
}
