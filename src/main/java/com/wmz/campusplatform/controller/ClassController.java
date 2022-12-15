package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.ClassDetailsConvert;
import com.wmz.campusplatform.details.ClassDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.pojo.Class;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.ClassService;
import com.wmz.campusplatform.service.NotifyService;
import com.wmz.campusplatform.service.PageService;
import com.wmz.campusplatform.service.TermService;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/class")
@Log4j2
public class ClassController {

    @Autowired
    private ClassDetailsConvert classDetailsConvert;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TermService termService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private PageService pageService;

    @Autowired
    private ClassService classService;

    @PostMapping("/saveClass")
    public ResultTool saveClass(@RequestBody ClassDetails classDetails){
        ResultTool resultTool = new ResultTool();
        ResultTool errorMessage = getErrorMessage(classDetails, false);
        if (errorMessage.getCode() != 200){
            return errorMessage;
        }
        Class aClass = classDetailsConvert.classDetailConvert(new Class(), classDetails);
        if (aClass.getUser() == null){
            aClass.setStatus(Status.ENROLL_TEACHER_IN_PROGRESS.getLabel());
        }else {
            aClass.setStatus(Status.ENROLL_TEACHER_FINISH.getLabel());
        }
        classRepository.save(aClass);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @PostMapping("/updateClass")
    public ResultTool updateClass(@RequestBody ClassDetails classDetails){
        ResultTool resultTool = new ResultTool();
        ResultTool errorMessage = getErrorMessage(classDetails, true);
        if (errorMessage.getCode() != 200){
            return errorMessage;
        }
        String termName = termService.getTermVerified(classDetails.getTermName());
        Class originalClass = classRepository.findByTermNameAndClassName(termName, classDetails.getClassName()).get(0);
        Class aClass = classDetailsConvert.classDetailConvert(originalClass, classDetails);
        if (aClass.getUser() == null){
            aClass.setStatus(Status.ENROLL_TEACHER_IN_PROGRESS.getLabel());
        }else if (Status.ENROLL_TEACHER_IN_PROGRESS.getLabel().equals(aClass.getStatus())){
            aClass.setStatus(Status.ENROLL_TEACHER_FINISH.getLabel());
        }
        classRepository.save(aClass);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @PostMapping("/updateTencentMeeting")
    public ResultTool updateTencentMeeting(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String tencentMeeting = (String) map.get("tencentMeeting");
        if (StringUtils.isEmpty(tencentMeeting)){
            resultTool.setCode(ReturnMessage.NULL_TENCENT_MEETING.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_TENCENT_MEETING.getCodeMessage());
            return resultTool;
        }else if (!classService.TencentMeetingValid(tencentMeeting)) {
            resultTool.setCode(ReturnMessage.INVALID_TENCENT_MEETING.getCodeNum());
            resultTool.setMessage(ReturnMessage.INVALID_TENCENT_MEETING.getCodeMessage());
            return resultTool;
        }
        String termName = termService.getTermVerified((String) map.get("termName"));
        String className = (String) map.get("className");
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, className);
        Class aClass = classList.get(0);
        aClass.setTencentMeeting((String) map.get("tencentMeeting"));
        classRepository.save(aClass);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getClassDataList")
    public ResultTool getClassDataList(@RequestParam(required = false) String query
                                     , @RequestParam(required = false) String termName
                                     , @RequestParam(required = false) String status
                                     , @RequestParam(required = false) String role
                                     , @RequestParam(required = false) String stuId
                                     , @RequestParam(required = false) Integer pageSize
                                     , @RequestParam(required = false) Integer pageIndex){
        ResultTool resultTool = new ResultTool();
        String term = termService.getTermVerified(termName);
        List<Class> classDetailsList = null;
        Integer offSet = pageSize * (pageIndex - 1);
        Map<String, Object> result = null;
        Integer classTotalSize = 0;
        if (Role.admin.name().equals(role)){
            classTotalSize = classRepository.findClassTotalSizeByTerm(query, term);
            classDetailsList = classRepository.findClassDataListByPage(query, term, PageRequest.of(pageIndex - 1, pageSize));
        }else if (Role.teacher.name().equals(role)){
            User teacher = userRepository.findByStuIdAndRole(stuId, role);
            Integer userId = teacher.getId();
            classTotalSize = classRepository.findMyTeachClassTotalSize(query, term, Status.START_CLASS_SUCCESS.getLabel(), userId);
            classDetailsList = classRepository.findMyTeachClassDataListByPage(query, term
                    , Status.START_CLASS_SUCCESS.getLabel(), userId, PageRequest.of(pageIndex - 1, pageSize));
        }else if (Role.student.name().equals(role)){
            User student = userRepository.findByStuIdAndRole(stuId, role);
            //get all class list
            classDetailsList = student.getClassList();
            //filter by termName and status == '已开班'
            classDetailsList = classDetailsList.stream().filter(classDetail ->
                    term.equals(classDetail.getCourse().getTerm().getTerm())
                            && Status.START_CLASS_SUCCESS.getLabel().equals(classDetail.getStatus())).collect(Collectors.toList());
            //filter by query
            if (!StringUtils.isEmpty(query)){
                classDetailsList = classDetailsList.stream()
                        .filter(classDetail -> !StringUtils.isEmpty(classDetail.getName()) && classDetail.getName().contains(query)
                                || classDetail.getCourse() != null && classDetail.getCourse().getName().contains(query)
                                || !StringUtils.isEmpty(classDetail.getDay()) && classDetail.getDay().contains(query)
                                || classDetail.getRoom() != null && classDetail.getRoom().getRoomName().contains(query)
                                || !StringUtils.isEmpty(classDetail.getTencentMeeting()) && classDetail.getTencentMeeting().contains(query)
                                || classDetail.getUser() != null && classDetail.getUser().getName().contains(query)).collect(Collectors.toList());
            }
            //get class list total size
            classTotalSize = classDetailsList.size();
            //get class by page
            classDetailsList = classDetailsList.subList(offSet, offSet + pageSize > classTotalSize ? classTotalSize : offSet + pageSize);
        }

        result = pageService.getPageData(classDetailsList, classTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(result);
        return resultTool;
    }

    @PostMapping("/deleteClass")
    public ResultTool deleteClass(@RequestBody Map<String, String> map){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified(map.get("termName"));
        String className = map.get("className");
        List<Class> aClass = classRepository.findByTermNameAndClassName(termName, className);
        classRepository.delete(aClass.get(0));
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getClassByStatus")
    public ResultTool getClassByStatus(@RequestParam(required = false) String query, String termName, String status,
                                       @RequestParam(required = false) Integer pageIndex,
                                       @RequestParam(required = false) Integer pageSize){
        ResultTool resultTool = new ResultTool();
        termName = termService.getTermVerified(termName);
        Integer offSet = pageSize * (pageIndex - 1);
        Integer classTotalSize = classRepository.getClassTotalSizeByStatusAndTerm(query, termName, status);
        List<Map<String, Object>> classList = classRepository.findByStatusAndTermNameByPage(query, termName, status, pageSize, offSet);
        List<ClassDetails> classDetailsList = new ArrayList<>();
        for (Map<String, Object> classData : classList) {
            ClassDetails classDetails = new ClassDetails(
                    (String) classData.get("className"),
                    (String) classData.get("courseName"),
                    (String)classData.get("roomName"),
                    (String) classData.get("day"));
            classDetails.setTeacherName((String) classData.get("teacherName"));
            classDetails.setTeacherWx((String) classData.get("teacherWx"));
            classDetails.setTeacherTel((String) classData.get("teacherTel"));
            classDetails.setTeacherClass((String) classData.get("teacherClass"));
            List<Date> dateList = new ArrayList<>();
            dateList.add((Date) classData.get("startTime"));
            dateList.add((Date) classData.get("endTime"));
            classDetails.setDateList(dateList);
            classDetailsList.add(classDetails);
        }
        Map<String, Object> pageData = pageService.getPageData(classDetailsList, classTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    @PostMapping("/changeStatus")
    public ResultTool changeStatus(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String status = (String) map.get("status");
        String className = (String) map.get("className");
        String termName = termService.getTermVerified((String) map.get("termName"));
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, className);
        Class aClass = classList.get(0);
        aClass.setStatus(status);
        if (aClass.getStatus().equals(Status.START_CLASS_SUCCESS.getLabel())){
            List<User> studentList = aClass.getStudentList();
            //send start class notify to student account
            String description = "您报名的 " + termName + " - " + map.get("className") + " 课程已开班，请至 我的课程 - 课程详情 查看具体信息。";
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.CLASS_STATUS_CHANGE.getLabel()
                    , studentList, description);

            //send start class notify to teacher account
            List<User> teacher = new ArrayList<>();
            teacher.add(aClass.getUser());
            String description2 = "您负责授课的 " + termName + " - " + map.get("className") + " 课程已开班，请至 我教的课 - 课程详情 查看具体信息，若为线上课程，请及时更新线上会议号";
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.CLASS_STATUS_CHANGE.getLabel()
                    , teacher, description2);
        }
        classRepository.save(aClass);



        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getStarTeacher")
    public ResultTool getStarTeacher(){
        ResultTool resultTool = new ResultTool();
        List<Map<String, Object>> starTeacher = classRepository.getStarTeacher();
        List<String> teacherList = new ArrayList<>();
        List<BigInteger> teachCntList = new ArrayList<>();
        int cnt = 0;
        for (Map<String, Object> teacher : starTeacher) {
            cnt++;
            teacherList.add((String) teacher.get("name"));
            teachCntList.add((BigInteger) teacher.get("teachCnt"));
            if (cnt == 5) break;
        }
        Map<String, List> map = new HashMap<>();
        map.put("teacherName", teacherList);
        map.put("teachCnt", teachCntList);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(map);
        return resultTool;
    }

    @GetMapping("/getClassStatistic")
    public ResultTool getClassStatistic(@RequestParam(required = false) String termName){
        ResultTool resultTool = new ResultTool();
        List<PieData> pieDataList = new ArrayList<>();
        List<Class> classList = new ArrayList<>();
        if (StringUtils.isEmpty(termName) || (!StringUtils.isEmpty(termName) && "所有学期".equals(termName))){
            classList = classRepository.findByStatus(Status.START_CLASS_SUCCESS.getLabel());
        }else{
            classList = classRepository.findByStatusAndTerm(Status.START_CLASS_SUCCESS.getLabel(), termName);
        }
        Map<String, List<Class>> courseCollection = classList.stream().collect(Collectors.groupingBy(aClass -> aClass.getCourse().getName()));
        for(Map.Entry<String, List<Class>> map : courseCollection.entrySet()){
            pieDataList.add(new PieData(map.getKey(), map.getValue().size()));
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pieDataList);
        return resultTool;
    }

    private ResultTool getErrorMessage(ClassDetails classDetails, boolean isUpdate){
        ResultTool resultTool = new ResultTool();
        String pattern = "d{9}";
        if (StringUtils.isEmpty(classDetails.getClassName())){
            resultTool.setCode(ReturnMessage.NULL_CLASS_NAME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_CLASS_NAME.getCodeMessage());
        }else if(!isUpdate && classRepository.findByTermNameAndClassName(classDetails.getTermName(), classDetails.getClassName()).size() != 0){
            resultTool.setCode(ReturnMessage.EXISTED_CLASS.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXISTED_CLASS.getCodeMessage());
        }else if (StringUtils.isEmpty(classDetails.getCourseName())){
            resultTool.setCode(ReturnMessage.NULL_COURSE_NAME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_COURSE_NAME.getCodeMessage());
        }else if (StringUtils.isEmpty(classDetails.getDay())){
            resultTool.setCode(ReturnMessage.NULL_CLASS_DAY.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_CLASS_DAY.getCodeMessage());
        }else if (classDetails.getDateList() == null){
            resultTool.setCode(ReturnMessage.NULL_CLASS_TIME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_CLASS_TIME.getCodeMessage());
        }else if (StringUtils.isEmpty(classDetails.getClassroom())){
            resultTool.setCode(ReturnMessage.NULL_CLASS_FORM.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_CLASS_FORM.getCodeMessage());
        }else if (classDetails.getMaxStudentCount() <= 0){
            resultTool.setCode(ReturnMessage.INVALID_STUDENT_UPPER_LIMIT.getCodeNum());
            resultTool.setMessage(ReturnMessage.INVALID_STUDENT_UPPER_LIMIT.getCodeMessage());
        }else {
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }
        return resultTool;
    }
}
