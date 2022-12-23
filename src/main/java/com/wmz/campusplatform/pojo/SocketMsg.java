package com.wmz.campusplatform.pojo;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @description 消息实体
 */
public class SocketMsg {
    private int type; //聊天类型0：群聊，1：单聊.
    private String fromUser;//发送者.
    private String toUser;//接受者.
    private String msg;//消息

    private List<String> tempFilePath;

    private List<String> suffixName;

    private List<String> fileName;

    private Boolean isFile;// is file or not

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getTempFilePath() {
        return tempFilePath;
    }

    public void setTempFilePath(List<String> tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

    public List<String> getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(List<String> suffixName) {
        this.suffixName = suffixName;
    }

    public Boolean getIsFile() {
        return isFile;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public List<String> getFileName() {
        return fileName;
    }

    public void setFileName(List<String> fileName) {
        this.fileName = fileName;
    }
}