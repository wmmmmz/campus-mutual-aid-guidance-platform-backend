package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.RoomRepository;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/room")
@Log4j2
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TermRepository termRepository;

    @GetMapping("/getFreeRoomByTerm")
    public ResultTool getFreeRoomByTerm(@RequestParam(required = false) String start
                                       ,@RequestParam(required = false) String end
                                       ,@RequestParam(required = false) String termName
                                       ,@RequestParam(required = false) String day
                                       ,@RequestParam(required = false) String className) throws ParseException {
        ResultTool resultTool = new ResultTool();
        List<Room> freeRoom = null;
        String startTime = "1970-01-01 ", endTime = "1970-01-01 ";
        if (start != null && end != null){
            startTime += start;
            endTime += end;
        }
        if (StringUtils.isEmpty(termName)) {
            Term termByDate = termRepository.findTermByDate(new Date());
            if (termByDate == null) {
                log.error("学期不存在");
            } else {
                termName = termByDate.getTerm();
            }
        }
        if (startTime != null && endTime != null && !StringUtils.isEmpty(day))
            freeRoom = roomRepository.findFreeRoomByTermNameAndTime(termName, startTime, endTime, day, className);
        else
            freeRoom= roomRepository.findAll();
        List<TreeSelectData> roomData = new ArrayList<>();
        for (Room room : freeRoom) {
            TreeSelectData treeSelectData = new TreeSelectData(room.getForm(), room.getRoomName());
            roomData.add(treeSelectData);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(roomData);
        return resultTool;
    }
}
