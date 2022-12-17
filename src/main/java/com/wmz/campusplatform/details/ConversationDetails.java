package com.wmz.campusplatform.details;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationDetails {
    private String name;

    private String stuId;

    private String latestMessage;

    private String avatar;

    private Integer unreadCnt;

    private String latestMessageTime;
}
