package com.wmz.campusplatform.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDetails {
    private String name;

    private String avatar;

    private Boolean myMessage;

    private String content;

    private String time;

    private Boolean isFile;

    private Boolean isImg;

    private String imgBase64;

    private List<String> srcList;//for picture preview list


}
