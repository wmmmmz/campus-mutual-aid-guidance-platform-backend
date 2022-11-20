package com.wmz.campusplatform.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @JsonIgnoreProperties(value = "receiverList")
    @ManyToMany(mappedBy = "receiverList")
    private List<NotifyAnnounce> notifyAnnounceList;

    @ManyToMany(mappedBy = "studentList")
    private List<Class> classList;
}
