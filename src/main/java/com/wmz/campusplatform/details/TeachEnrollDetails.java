package com.wmz.campusplatform.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.Inet4Address;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeachEnrollDetails {

    private String studentName;

    private String studentClass;

    private String studentTel;

    private String studentWx;

    private String className;

    private String courseName;

    private String classroom;

    private String day;

    @JsonFormat(pattern = "HH:mm")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enrollDate;


    private String interviewDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date successDate;

    private String interviewLink;

    private Integer active;

    private String status;

    private String resumeUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interviewStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date interviewEndTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date passDate;

    public TeachEnrollDetails(String studentName, String studentClass, String studentTel, String studentWx
            , String className, String courseName, String classroom, String day, Date startTime, Date endTime
            , Date enrollDate, Date successDate, String interviewLink, String status, String resumeUrl
            , Date interviewStartTime, Date interviewEndTime, Date passDate) {
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.studentTel = studentTel;
        this.studentWx = studentWx;
        this.className = className;
        this.courseName = courseName;
        this.classroom = classroom;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enrollDate = enrollDate;
        this.successDate = successDate;
        this.interviewLink = interviewLink;
        this.status = status;
        this.resumeUrl = resumeUrl;
        this.interviewStartTime = interviewStartTime;
        this.interviewEndTime = interviewEndTime;
        this.passDate = passDate;
    }
}
