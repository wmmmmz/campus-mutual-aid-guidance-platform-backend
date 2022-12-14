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

    NULL_COURSE_NAME(413, "课程名不能为空"),

    NULL_CLASS_TIME(414, "上课始末时间不能为空"),

    NULL_CLASS_DAY(415, "课程日期不能为空"),

    NULL_CLASS_FORM(416, "上课形式不能为空"),

    NULL_CLASS_NAME(417, "班级名不能为空"),

    EXISTED_CLASS(418, "本学期该班级已存在"),

    EXIST_TEACH_ENROLL(419, "请勿重复报名"),

    NULL_FILE(420, "请上传简历"),

    NULL_INTERVIEW_LINK(421, "请输入预定的会议号"),

    NULL_INTERVIEW_TIME(422, "请输入面试时间"),

    INVALID_INTERVIEW_LINK(413, "会议号格式错误，应为九位数字"),

    CLASS_ASSIGNED_TEACHER(414, "该班级已分配导生"),

    PASSED_EXIST(415, "该班级已有候选人在选择offer"),

    NULL_REFUSE_REASON(416, "请填写具体原因"),

    EXIST_CLASS_ENROLL(417, "请勿重复报名"),

    IS_TEACHER(418, "您是该班级的导生"),

    INVALID_STUDENT_UPPER_LIMIT(419, "学生上限数不合法"),

    NULL_STUDENT_LIST(420, "请先选择学生"),

    LOCKED_ACCOUNT(421, "账号已锁定，请联系超级管理员解锁"),

    NULL_OLD_PASSWORD(422, "旧密码不能为空"),

    WRONG_OLD_PASSWORD(422, "旧密码错误"),

    NULL_NEW_PASSWORD(422, "新密码不能为空"),

    NULL_TENCENT_MEETING(424, "请输入预定的会议号"),

    INVALID_TENCENT_MEETING(425, "会议号格式错误，应为九位数字"),

    CONVERSATION_EXIST(426, "聊天已存在"),

    NULL_CONTENT(427, "请先输入"),

    NO_PERMISSION(428, "无权限"),

    NO_MORE(429, "没有更多啦");
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
