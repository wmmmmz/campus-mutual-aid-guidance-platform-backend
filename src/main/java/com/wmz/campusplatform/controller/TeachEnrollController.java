package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.TeachEnrollDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.FileUploadService;
import com.wmz.campusplatform.service.MongoDBService;
import com.wmz.campusplatform.service.TermService;
import com.wmz.campusplatform.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/teachEnroll")
public class TeachEnrollController {

    @Autowired
    private TermService termService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private MongoDBService mongoDBService;

    @PostMapping("/checkEnroll")
    public ResultTool checkEnroll(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String termName = termService.getTermVerified((String) map.get("termName"));
        Integer classId = classRepository.findByTermNameAndClassName(termName, (String) map.get("className")).get(0).getId();
        User student = userRepository.findByStuIdAndRole((String) map.get("stuId"), "student");
        User teacher = userRepository.findByStuIdAndRole((String) map.get("stuId"), "teacher");
        if ((student != null && classRepository.findTeachEnroll(student.getId(), classId).size() != 0)
                || (teacher != null && classRepository.findTeachEnroll(teacher.getId(), classId).size() != 0)){
            resultTool.setCode(ReturnMessage.EXIST_TEACH_ENROLL.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXIST_TEACH_ENROLL.getCodeMessage());
        }else {
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }
        return resultTool;
    }


    @PostMapping("/saveTeachEnroll")
    public ResultTool saveTeachEnroll(@RequestBody Map<String, Object> map) throws IOException {
        ResultTool resultTool = new ResultTool();
        String filePath = (String) map.get("filePath");
        if (StringUtils.isEmpty(filePath)){
            resultTool.setCode(ReturnMessage.NULL_FILE.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_FILE.getCodeMessage());
            return resultTool;
        }
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
        String resumeName = map.get("stuId") + "_" + termName + "_" + map.get("className");
        java.io.File resumeFile = new java.io.File(filePath);
        mongoDBHelper.save(new UploadFile(mongoDBHelper.findAll(UploadFile.class).size() + 1, (String) map.get("suffixName")
                , resumeName, fileUploadService.fileToByte(resumeFile)));
        classRepository.saveTeachEnroll(userId, classId, new Date(), resumeName);
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
            String fileName = stuId + "_" + termName + "_" + teachData.get("className");
            List<UploadFile> resumeList = mongoDBService.getFileByFileName(fileName);
            UploadFile resume = null;
            String prefix = "";
            byte[] resumeByte = new byte[0];
            if (resumeList.size() != 0){
                resume = resumeList.get(0);
                resumeByte = resume.getFile();
                String filePre = resume.getFilePre();
                prefix = fileUploadService.getBase64PrefixByFileSuffix(filePre);
            }

            TeachEnrollDetails teachEnrollDetail = new TeachEnrollDetails(
                    (String) teachData.get("className"),
                    (String) teachData.get("courseName"),
                    (String) teachData.get("classroom"),
                    (String) teachData.get("day"),
                    (Date) teachData.get("startTime"),
                    (Date) teachData.get("endTime"),
                    (Date) teachData.get("enrollDate"),
                    (Date) teachData.get("successDate"),
                    (String) teachData.get("interviewLink"),
                    (String) teachData.get("status"),
                    prefix + Base64.getEncoder().encodeToString(resumeByte)
            );
            String interviewDate = "";
            if (teachData.get("interview_start_date") != null && teachData.get("interview_end_date") != null){
                Date interviewStartTime = (Date) teachData.get("interview_start_date");
                Date interviewEndTime = (Date) teachData.get("interview_end_date");
                SimpleDateFormat formatterStart = new SimpleDateFormat( "yyyy-MM-dd HH:mm");
                SimpleDateFormat formatterEnd = new SimpleDateFormat( "HH:mm");
                interviewDate = formatterStart.format(interviewStartTime) + " - " + formatterEnd.format(interviewEndTime);
            }else {
                interviewDate = "";
            }
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
            teachEnrollDetail.setInterviewDate(interviewDate);
            teachEnrollDetails.add(teachEnrollDetail);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(teachEnrollDetails);
        return resultTool;
    }
}
