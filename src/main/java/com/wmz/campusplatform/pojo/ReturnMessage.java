package com.wmz.campusplatform.pojo;

public enum ReturnMessage {
    SUCCESS_CODE(200,"success"),

    WRONG_USERNAME_OR_PASSWORD(401, "用户名或密码错误"),

    WRONG_IDENTITY(402, "身份选择错误"),

    NOT_LOGIN(403, "未登陆"),

    NO_USER(405, "用户不存在"),

    EXISTED_TERM(406, "学期已存在"),

    NULL_TERM_NAME(407, "学期名不能为空"),

    NULL_TERM_TIME(408, "学期始末时间不能为空"),

    TERM_UNEXISTED(409, "学期不存在"),

    NULL_NOTIFY_TITLE(410, "通知主题不能为空"),

    NULL_NOTIFY_CONTENT(411, "通知内容不能为空"),

    NULL_NOTIFY_RECEIVER(412, "通知对象不能为空"),

    NULL_COURSE_NAME(413, "课程名不能为空");

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
