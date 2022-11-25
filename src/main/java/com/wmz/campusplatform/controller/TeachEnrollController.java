package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.TeachEnrollDetails;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.pojo.Status;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teachEnroll")
public class TeachEnrollController {

    @Autowired
    private TermService termService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @PostMapping("/saveTeachEnroll")
    public ResultTool saveTeachEnroll(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified((String) map.get("termName"));
        Integer classId = classRepository.findByTermNameAndClassName(termName, (String) map.get("className")).get(0).getId();
        Integer userId = userRepository.findByStuIdAndRole((String) map.get("stuId"), (String) map.get("role")).getId();
        User student = userRepository.findByStuIdAndRole((String) map.get("stuId"), "student");
        User teacher = userRepository.findByStuIdAndRole((String) map.get("stuId"), "teacher");
        if ((student != null && classRepository.findTeachEnroll(student.getId(), classId).size() != 0)
                || (teacher != null && classRepository.findTeachEnroll(teacher.getId(), classId).size() != 0)){
            resultTool.setCode(ReturnMessage.EXIST_TEACH_ENROLL.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXIST_TEACH_ENROLL.getCodeMessage());
            return resultTool;
        }
        classRepository.saveTeachEnroll(userId, classId, new Date());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getTeachEnrollDataList")
    public ResultTool getTeachEnrollDataList(@RequestParam(required = false) String query,
                                             @RequestParam String termName,
                                             @RequestParam String stuId){
        ResultTool resultTool = new ResultTool();
        termName = termService.getTermVerified(termName);
        List<TeachEnrollDetails> teachEnrollDetails = new ArrayList<>();
        List<Map<String, Object>> teachEnrollDataList = classRepository.getTeachEnrollDataList(query, termName, stuId);
        for (Map<String, Object> teachData : teachEnrollDataList) {
            TeachEnrollDetails teachEnrollDetail = new TeachEnrollDetails(
                    (String) teachData.get("className"),
                    (String) teachData.get("courseName"),
                    (String) teachData.get("classroom"),
                    (String) teachData.get("day"),
                    (Date) teachData.get("startTime"),
                    (Date) teachData.get("endTime"),
                    (Date) teachData.get("enrollDate"),
                    (Date) teachData.get("interviewDate"),
                    (Date) teachData.get("successDate"),
                    (String) teachData.get("interviewLink"),
                    (String) teachData.get("status")
            );
            String status = (String) teachData.get("status");
            if (Status.ENROLLED.getLabel().equals(status)){
                teachEnrollDetail.setActive(1);
            }else if (Status.INTERVIEWING.getLabel().equals(status)){
                teachEnrollDetail.setActive(2);
            }else if (Status.HIRED.getLabel().equals(status)){
                teachEnrollDetail.setActive(3);
            }else if (Status.TERMINATION.getLabel().equals(status)){
                teachEnrollDetail.setActive(-1);
            }
            teachEnrollDetails.add(teachEnrollDetail);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(teachEnrollDetails);
        return resultTool;
    }
}
