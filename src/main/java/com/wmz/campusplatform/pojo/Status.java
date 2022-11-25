package com.wmz.campusplatform.pojo;

public enum Status {
    NO_NEED,
    ENROLL_TEACHER_IN_PROGRESS("招募导生中"),
    ENROLL_STUDENT_IN_PROGRESS("学生报名中"),
    ENROLL_TEACHER_FINISH("招募导生完成"),
    ENROLL_STUDENT_FINISH("学生报名截止"),
    START_CLASS_SUCCESS("已开班"),
    ENROLLED("报名成功"),
    INTERVIEWING("安排面试"),
    HIRED("成为导生"),
    TERMINATION("流程终止"),
    UNREADED,
    READED;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    Status(String label) {
        this.label = label;
    }

    Status() {
    }

    private String label;


}
