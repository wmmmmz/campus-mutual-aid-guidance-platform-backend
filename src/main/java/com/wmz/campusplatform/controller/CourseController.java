package com.wmz.campusplatform.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wmz.campusplatform.details.CourseDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.CourseRepository;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.service.PageService;
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
@RequestMapping("/course")
@Log4j2
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private TermService termService;

    @Autowired
    private PageService pageService;

    @SaCheckRole("admin")
    @GetMapping("/getCourseDataList")
    public ResultTool getCourseDataList(@RequestParam(required = false) String query, String termName
                                       ,@RequestParam(required = false) Integer pageSize
                                       ,@RequestParam(required = false) Integer pageIndex) {
        ResultTool resultTool = new ResultTool();
        termName = termService.getTermVerified(termName);
        List<CourseDetails> courseDetailsList = new ArrayList<>();
        Integer offSet = pageSize * (pageIndex - 1);
        Integer totalSize = courseRepository.findCourseTotalSizeByTerm(query, termName);
        List<Map<String, Object>> courseDetailList = courseRepository.findCourseDetailListByPage(query, termName, pageSize, offSet);
        for (Map<String, Object> courseDetail : courseDetailList) {
            CourseDetails courseDetails = new CourseDetails((String) courseDetail.get("name"), (BigInteger) courseDetail.get("classCnt"));
            courseDetailsList.add(courseDetails);
        }
        Map<String, Object> pageData = pageService.getPageData(courseDetailsList, totalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    @SaCheckRole("admin")
    @PostMapping("/saveCourse")
    public ResultTool saveCourse(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String courseName = (String) map.get("courseName");
        if (StringUtils.isEmpty(courseName)){
            resultTool.setCode(ReturnMessage.NULL_COURSE_NAME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_COURSE_NAME.getCodeMessage());
            return resultTool;
        }
        String termName = (String)map.get("termName");
        Term term = termRepository.findByTerm(termName);
        Course course = new Course();
        course.setTerm(term);
        course.setName(courseName);
        courseRepository.save(course);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @SaCheckRole("admin")
    @PostMapping("/updateCourse")
    public ResultTool  updateCourse(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String courseName = (String) map.get("courseName");
        if (StringUtils.isEmpty(courseName)){
            resultTool.setCode(ReturnMessage.NULL_COURSE_NAME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_COURSE_NAME.getCodeMessage());
            return resultTool;
        }
        String originalCourseName = (String)map.get("originalCourseName");
        String termName = (String)map.get("termName");
        courseRepository.updateCourseName(courseName, originalCourseName, termName);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @SaCheckRole("admin")
    @PostMapping("/deleteCourse")
    public ResultTool deleteCourse(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String courseName = (String) map.get("courseName");
        String termName = (String)map.get("termName");
        courseRepository.deleteCourse(courseName, termName);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @SaCheckRole("admin")
    @GetMapping("/getCourseByTerm")
    public ResultTool getCourseByTerm(@RequestParam String termName){
        ResultTool resultTool = new ResultTool();
        if (StringUtils.isEmpty(termName)) {
            Term termByDate = termRepository.findTermByDate(new Date());
            if (termByDate == null) {
                log.error("???????????????");
            } else {
                termName = termByDate.getTerm();
            }
        }
        List<TreeSelectData> treeSelectDataList = new ArrayList<>();
        List<Map<String, Object>> courseDetailList = courseRepository.findCourseDetailList(null, termName);
        for (Map<String, Object> courseDetail : courseDetailList) {
            treeSelectDataList.add(new TreeSelectData((String)courseDetail.get("name"), (String)courseDetail.get("name")));
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(treeSelectDataList);
        return resultTool;
    }
}
