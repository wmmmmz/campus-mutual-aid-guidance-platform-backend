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
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne
    private Course course;

    @ManyToOne
    private User user;

    @ManyToOne
    private Room room;

    private String TencentMeeting;

    private String day;

    @JsonFormat(pattern = "HH:mm", timezone="GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm", timezone="GMT+8")
    private Date endTime;

    private String status;

    private Integer maxStudentCount;

    //class的学生列表
    @ManyToMany
    @JsonIgnoreProperties("classList")
    @JoinTable(name = "student_enroll_class",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> studentList;

    //class的导生报名列表
    @ManyToMany
    @JsonIgnore
    @JsonIgnoreProperties("teachEnrollClassList")
    @JoinTable(name = "teach_enroll",
            joinColumns = @JoinColumn(name = "class_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> interviewList;


    public List<User> getInterviewList() {
        return interviewList;
    }

    public void setInterviewList(List<User> interviewList) {
        this.interviewList = interviewList;
    }

    public Class() {
    }

    public Class(String className, Course course, String day, Date startTime, Date endTime, Room room
    , String tencentMeeting, User user,  String status, List<User> studentList){
        this.name = className;
        this.course = course;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.TencentMeeting = tencentMeeting;
        this.user = user;
        this.status = status;
        this.studentList = studentList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public String getTencentMeeting() {
        return TencentMeeting;
    }

    public void setTencentMeeting(String tencentMeeting) {
        TencentMeeting = tencentMeeting;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMaxStudentCount() {
        return maxStudentCount;
    }

    public void setMaxStudentCount(Integer maxStudentCount) {
        this.maxStudentCount = maxStudentCount;
    }

    public List<User> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<User> studentList) {
        this.studentList = studentList;
    }

    @Override
    public String toString() {
        return "Class{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", course=" + course +
                ", user=" + user +
                ", room=" + room +
                ", TencentMeeting='" + TencentMeeting + '\'' +
                ", day='" + day + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}
