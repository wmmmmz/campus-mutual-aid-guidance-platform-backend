package com.wmz.campusplatform.pojo;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;

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


}
