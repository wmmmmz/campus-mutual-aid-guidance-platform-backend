package com.wmz.campusplatform.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private String stuId;

    private String pwd;

    private String name;

    private String className;

    private String tel;

    private String wx;

    private String role;

    private String description;

    private String saTokenValue;

    private String imgBase64;
}
