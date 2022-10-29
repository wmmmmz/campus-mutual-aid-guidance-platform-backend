package com.wmz.campusplatform.pojo;

public enum ReturnMessage {
    SUCCESS_CODE(200,"success"),

    WRONG_USERNAME_OR_PASSWORD(401, "用户名或密码错误"),

    WRONG_IDENTITY(402, "身份选择错误"),

    NOT_LOGIN(403, "未登陆"),

    NO_USER(405, "用户不存在");

    private int codeNum;

    private String codeMessage;

    public int getCodeNum() {
        return codeNum;
    }

    public String getCodeMessage() {
        return codeMessage;
    }

    ReturnMessage(int codeNum, String codeMessage) {
        this.codeNum = codeNum;
        this.codeMessage = codeMessage;
    }
}
