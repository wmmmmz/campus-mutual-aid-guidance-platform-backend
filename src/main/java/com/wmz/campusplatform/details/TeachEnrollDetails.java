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

    private String className;

    private String courseName;

    private String classroom;

    private String day;

    @JsonFormat(pattern = "HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date enrollDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date interviewDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date successDate;

    private String interviewLink;

    private Integer active;

    private String status;

    public TeachEnrollDetails(String className, String courseName, String classroom, String day, Date startTime, Date endTime, Date enrollDate, Date interviewDate, Date successDate, String interviewLink, String status) {
        this.className = className;
        this.courseName = courseName;
        this.classroom = classroom;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.enrollDate = enrollDate;
        this.interviewDate = interviewDate;
        this.successDate = successDate;
        this.interviewLink = interviewLink;
        this.status = status;
    }
}
