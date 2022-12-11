package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.ClassDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.PageService;
import com.wmz.campusplatform.service.TermService;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classEnroll")
@Log4j2
public class ClassEnrollController {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TermService termService;

    @Autowired
    private PageService pageService;

    @PostMapping("/saveEnroll")
    public ResultTool saveEnroll(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified((String) map.get("termName"));
        Integer classId = classRepository.findByTermNameAndClassName(termName, (String) map.get("className")).get(0).getId();
        User user = userRepository.findByStuIdAndRole((String) map.get("stuId"), Role.student.name());
        Integer userId = user.getId();
        String teachEnrollStatus = classRepository.findStatusByUsernameAndClassId(user.getName(), classId);
        //teacher of this class can't be a student
        if (!StringUtils.isEmpty(teachEnrollStatus) && teachEnrollStatus.equals(Status.HIRED.getLabel())){
            resultTool.setCode(ReturnMessage.IS_TEACHER.getCodeNum());
            resultTool.setMessage(ReturnMessage.IS_TEACHER.getCodeMessage());
            return resultTool;
        }
        //enrolled
        if (classRepository.findClassEnroll(userId, classId).size() != 0){
            resultTool.setCode(ReturnMessage.EXIST_CLASS_ENROLL.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXIST_CLASS_ENROLL.getCodeMessage());
            return resultTool;
        }
        //save
        classRepository.saveClassEnroll(userId, classId, new Date());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getClassEnrollDataList")
    public ResultTool getClassEnrollDataList(@RequestParam(required = false) String query, String termName, String stuId,
                                             @RequestParam(required = false) Integer pageIndex,
                                             @RequestParam(required = false) Integer pageSize){
        ResultTool resultTool = new ResultTool();
        String term = termService.getTermVerified(termName);
        Integer offSet = pageSize * (pageIndex - 1);
        Integer userId = userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId();
        Integer myClassEnrollTotalSize = classRepository.getMyClassEnrollTotalSizeByTerm(query, term, userId);
        List<Map<String, Object>> myClassEnrollDataList = classRepository.getMyClassEnrollDataListByPage(query, term, userId, pageSize, offSet);
        List<ClassDetails> myClassDetailsList = new ArrayList<>();
        for (Map<String, Object> classData : myClassEnrollDataList) {
            ClassDetails classDetail = new ClassDetails(
                    (String) classData.get("className"),
                    (String) classData.get("courseName"),
                    (String) classData.get("teacherName"),
                    (String) classData.get("teacherTel"),
                    (String) classData.get("teacherWx"),
                    (String) classData.get("teacherClass"),
                    (String)classData.get("roomName"),
                    (String) classData.get("day"),
                    (BigInteger) classData.get("studentCnt"),
                    (String) classData.get("status")
            );
            List<Date> dateList = new ArrayList<>();
            dateList.add((Date) classData.get("startTime"));
            dateList.add((Date) classData.get("endTime"));
            classDetail.setDateList(dateList);
            classDetail.setTencentMeeting((String) classData.get("tencentMeeting"));
            classDetail.setTencentMeetingUrl(Status.TENCENT_MEETING_URL.getLabel() + classData.get("tencentMeeting"));
            myClassDetailsList.add(classDetail);
        }
        Map<String, Object> pageData = pageService.getPageData(myClassDetailsList, myClassEnrollTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    @PostMapping("/deleteClassEnroll")
    public ResultTool deleteClassEnroll(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified((String) map.get("termName"));
        Integer classId = classRepository.findByTermNameAndClassName(termName, (String) map.get("className")).get(0).getId();
        User user = userRepository.findByStuIdAndRole((String) map.get("stuId"), Role.student.name());
        Integer userId = user.getId();
        classRepository.deleteClassEnroll(classId, userId);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    /**
     * 学生/导生课程表
     * @param term
     * @param stuId
     * @return
     */
    @GetMapping("/getClassTimeTable")
    public ResultTool getClassTimeTable(@RequestParam String term, String stuId, String role){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified(term);
        User user = userRepository.findByStuIdAndRole(stuId, role);
        Integer userId = user.getId();
        List<Map<String, Object>> classTable = null;
        if (role.equals(Role.student.name())){
            classTable = classRepository.getClassTable(userId, termName);
        } else if (role.equals(Role.teacher.name())){
            classTable = classRepository.getTeachTable(userId, termName);
        }
        List<ClassTable> classTables = new ArrayList<>();
        classTables.add(new ClassTable("15:00 - 16:00"));
        classTables.add(new ClassTable("16:00 - 17:00"));
        classTables.add(new ClassTable("18:00 - 19:00"));
        classTables.add(new ClassTable("19:00 - 20:00"));
        for (Map<String, Object> table : classTable) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String timeValue = format.format((Date) table.get("start_time"));
            switch (timeValue){
                case "15:00":
                    fillClassTable(0, table, classTables);
                    break;
                case "16:00":
                    fillClassTable(1, table, classTables);
                    break;
                case "18:00":
                    fillClassTable(2, table, classTables);
                    break;
                case "19:00":
                    fillClassTable(3, table, classTables);
                    break;
            }
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(classTables);
        return resultTool;
    }

    private List<ClassTable> fillClassTable(Integer num, Map<String, Object> table, List<ClassTable> classTables){
        boolean isOnline = "腾讯会议".equals(table.get("room_name"));
        switch ((String) table.get("day")){
            case "一":
                classTables.get(num).setMondayClass((String) table.get("name"));
                classTables.get(num).setMondayRoom((String) table.get("room_name"));
                if (isOnline)
                    classTables.get(num).setMondayTencent((String) table.get("tencent_meeting"));
                break;
            case "二":
                classTables.get(num).setTuesdayClass((String) table.get("name"));
                classTables.get(num).setTuesdayRoom((String) table.get("room_name"));
                if (isOnline)
                    classTables.get(num).setTuesdayTencent((String) table.get("tencent_meeting"));
                break;
            case "三":
                classTables.get(num).setWednesdayClass((String) table.get("name"));
                classTables.get(num).setWednesdayRoom((String) table.get("room_name"));
                if (isOnline)
                    classTables.get(num).setWednesdayTencent((String) table.get("tencent_meeting"));
                break;
            case "四":
                classTables.get(num).setThursdayClass((String) table.get("name"));
                classTables.get(num).setThursdayRoom((String) table.get("room_name"));
                if (isOnline)
                    classTables.get(num).setThursdayTencent((String) table.get("tencent_meeting"));
                break;
            case "五":
                classTables.get(num).setFridayClass((String) table.get("name"));
                classTables.get(num).setFridayRoom((String) table.get("room_name"));
                if (isOnline)
                    classTables.get(num).setFridayTencent((String) table.get("tencent_meeting"));
                break;
        }
        return classTables;
    }
}
