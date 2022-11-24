package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.TermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
}
