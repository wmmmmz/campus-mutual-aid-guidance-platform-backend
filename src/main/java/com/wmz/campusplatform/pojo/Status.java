package com.wmz.campusplatform.pojo;

public enum Status {
    NO_NEED,
    IN_PROGRESS("流程中"),
    START_CLASS_SUCCESS("已开班"),
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
