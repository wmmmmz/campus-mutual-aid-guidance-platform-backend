package com.wmz.campusplatform.details;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassDetails {

    private String className;

    private String courseName;

    private String teacherName;

    private String teacherTel;

    private String teacherWx;

    private String teacherClass;

    private String classroom;

    private String day;

    @JsonFormat(pattern = "HH:mm:ss")
    private List<Date> dateList;

    @JsonFormat(pattern = "HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private Date endTime;

    private String termName;

    private BigInteger studentCnt;

    private String status;

    public ClassDetails(String className, String courseName, String teacherName, String teacherTel, String teacherWx
            , String teacherClass, String classroom, String day, BigInteger studentCnt, String status) {
        this.className = className;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.teacherTel = teacherTel;
        this.teacherWx = teacherWx;
        this.teacherClass = teacherClass;
        this.classroom = classroom;
        this.day = day;
        this.studentCnt = studentCnt;
        this.status = status;
    }
}
