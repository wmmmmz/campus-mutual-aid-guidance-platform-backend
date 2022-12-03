package com.wmz.campusplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassTable {
    private String time;

    private String MondayClass;

    private String MondayRoom;

    private String MondayTencent;

    private String TuesdayClass;

    private String TuesdayRoom;

    private String TuesdayTencent;

    private String WednesdayClass;

    private String WednesdayRoom;

    private String WednesdayTencent;

    private String ThursdayClass;

    private String ThursdayRoom;

    private String ThursdayTencent;

    private String FridayClass;

    private String FridayRoom;

    private String FridayTencent;

    public ClassTable(String time) {
        this.time = time;
    }
}
