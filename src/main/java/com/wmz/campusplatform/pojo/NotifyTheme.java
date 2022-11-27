package com.wmz.campusplatform.pojo;

public enum NotifyTheme {
    INTERVIEW_STATUS_CHANGE("面试流程更新");

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
