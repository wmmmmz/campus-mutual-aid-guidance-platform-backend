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
public class NotifyAnnounceDetails {

    private String senderName;

    private String senderClassName;

    private String senderTel;

    private String senderWx;

    private String senderStuId;

    private String senderRole;

    private String title;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    private List<String> receiverRole;

    private BigInteger unreadCnt;

}
