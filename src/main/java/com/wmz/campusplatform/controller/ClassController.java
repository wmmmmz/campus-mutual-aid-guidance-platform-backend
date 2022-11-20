package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.ClassDetailsConvert;
import com.wmz.campusplatform.details.ClassDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.pojo.Class;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.service.TermService;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/saveClass")
    public ResultTool saveClass(@RequestBody ClassDetails classDetails){
        ResultTool resultTool = new ResultTool();
        ResultTool errorMessage = getErrorMessage(classDetails, false);
        if (errorMessage.getCode() != 200){
            return errorMessage;
        }
        Class aClass = classDetailsConvert.classDetailConvert(new Class(), classDetails);
        aClass.setStatus(Status.IN_PROGRESS.getLabel());
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
        classRepository.save(aClass);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getClassDataList")
    public ResultTool getClassDataList(@RequestParam(required = false) String query, String termName){
        ResultTool resultTool = new ResultTool();
        if (StringUtils.isEmpty(termName)) {
            Term termByDate = termRepository.findTermByDate(new Date());
            if (termByDate == null) {
                log.error("学期不存在");
            } else {
                termName = termByDate.getTerm();
            }
        }
        List<Map<String, Object>> classDataList = classRepository.findClassDataList(query, termName);
        List<ClassDetails> classDetailsList = new ArrayList<>();
        for (Map<String, Object> classData : classDataList) {
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
            classDetailsList.add(classDetail);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(classDetailsList);
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

    private ResultTool getErrorMessage(ClassDetails classDetails, boolean isUpdate){
        ResultTool resultTool = new ResultTool();
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
        }else {
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }
        return resultTool;
    }
}
