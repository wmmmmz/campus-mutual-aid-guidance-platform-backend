package com.wmz.campusplatform.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDetails {
    private String name;

    private String avatar;

    private Boolean myMessage;

    private String content;

    private String time;
}
