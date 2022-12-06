package com.wmz.campusplatform.pojo;

public enum NotifyTheme {
    INTERVIEW_STATUS_CHANGE("面试流程更新"),

    CLASS_STATUS_CHANGE("课程状态更新"),

    SYSTEM_NOTIFY("系统通知");
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    NotifyTheme(String label) {
        this.label = label;
    }

    NotifyTheme() {
    }

    private String label;

}
