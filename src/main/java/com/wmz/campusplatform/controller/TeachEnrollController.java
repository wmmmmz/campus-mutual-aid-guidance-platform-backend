package com.wmz.campusplatform.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wmz.campusplatform.details.TeachEnrollDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.pojo.Class;
import com.wmz.campusplatform.repository.ClassRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.*;
import com.wmz.campusplatform.utils.MongoAutoIdUtil;
import com.wmz.campusplatform.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

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

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private MongoAutoIdUtil mongoAutoIdUtil;

    @Autowired
    private PageService pageService;

    @Autowired
    private ClassService classService;

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
        User user = userRepository.findByStuIdAndRole((String) map.get("stuId"), (String) map.get("role"));
        Integer userId = user.getId();
        User student = userRepository.findByStuIdAndRole((String) map.get("stuId"), "student");
        User teacher = userRepository.findByStuIdAndRole((String) map.get("stuId"), "teacher");
        if ((student != null && classRepository.findTeachEnroll(student.getId(), classId).size() != 0)
                || (teacher != null && classRepository.findTeachEnroll(teacher.getId(), classId).size() != 0)){
            resultTool.setCode(ReturnMessage.EXIST_TEACH_ENROLL.getCodeNum());
            resultTool.setMessage(ReturnMessage.EXIST_TEACH_ENROLL.getCodeMessage());
            return resultTool;
        }
        String resumeName = user.getName() + "_" + termName + "_" + map.get("className");
        java.io.File resumeFile = new java.io.File(filePath);
        mongoDBHelper.save(new UploadFile(mongoAutoIdUtil.getNextSequence("seq_uploadFile"), (String) map.get("suffixName")
                , resumeName, fileUploadService.fileToByte(resumeFile)));
        classRepository.saveTeachEnroll(userId, classId, new Date(), resumeName);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getTeachEnrollDataList")
    public ResultTool getTeachEnrollDataList(@RequestParam(required = false) String query,
                                             @RequestParam String termName,
                                             @RequestParam(required = false) String stuId
                                           , @RequestParam(required = false) Integer pageSize
                                           , @RequestParam(required = false) Integer pageIndex){
        ResultTool resultTool = new ResultTool();
        termName = termService.getTermVerified(termName);
        Integer offSet = pageSize * (pageIndex - 1);
        List<TeachEnrollDetails> teachEnrollDetails = new ArrayList<>();
        List<Map<String, Object>> teachEnrollDataList = null;
        Integer teachEnrollTotalSize = 0;
        if (!StringUtils.isEmpty(stuId)){
            teachEnrollTotalSize = classRepository.getTeachEnrollTotalSize(query, termName, stuId);
            teachEnrollDataList  = classRepository.getTeachEnrollDataListByPage(query, termName, stuId, pageSize, offSet);
        }else {
            teachEnrollTotalSize = classRepository.getAllTeachEnrollTotalSizeByTerm(query, termName);
            teachEnrollDataList = classRepository.getAllTeachEnrollDataListByPage(query, termName, pageSize, offSet);
        }
        for (Map<String, Object> teachData : teachEnrollDataList) {
            String fileName = teachData.get("studentName") + "_" + termName + "_" + teachData.get("className");
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
                    (String) teachData.get("studentName"),
                    (String) teachData.get("studentClass"),
                    (String) teachData.get("studentTel"),
                    (String) teachData.get("studentWx"),
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
                    prefix + Base64.getEncoder().encodeToString(resumeByte),
                    (Date) teachData.get("interview_start_date"),
                    (Date) teachData.get("interview_end_date"),
                    (Date) teachData.get("passDate"),
                    (String) teachData.get("remark")
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
            }else if (Status.PASSED.getLabel().equals(status)){
                teachEnrollDetail.setActive(3);
            }else if (Status.HIRED.getLabel().equals(status)){
                teachEnrollDetail.setActive(4);
            }else if (Status.TERMINATION.getLabel().equals(status)){
                teachEnrollDetail.setActive(-1);
            }else if (Status.INTERRUPTED.getLabel().equals(status)){
                teachEnrollDetail.setActive(-1);
            }
            teachEnrollDetail.setInterviewDate(interviewDate);
            teachEnrollDetails.add(teachEnrollDetail);
        }
        Map<String, Object> pageData = pageService.getPageData(teachEnrollDetails, teachEnrollTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    @SaCheckRole("admin")
    @PostMapping("/updateStatusToArrangeInterview")
    public ResultTool updateStatusToArrangeInterview(@RequestBody Map<String, Object> map) throws ParseException {
        ResultTool resultTool = new ResultTool();
        if (StringUtils.isEmpty((String) map.get("interviewLink"))){
            resultTool.setCode(ReturnMessage.NULL_INTERVIEW_LINK.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_INTERVIEW_LINK.getCodeMessage());
            return resultTool;
        }else if (map.get("startTime") == null || map.get("endTime") == null){
            resultTool.setCode(ReturnMessage.NULL_INTERVIEW_TIME.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_INTERVIEW_TIME.getCodeMessage());
            return resultTool;
        }else if (!classService.TencentMeetingValid((String) map.get("interviewLink"))){
            resultTool.setCode(ReturnMessage.INVALID_INTERVIEW_LINK.getCodeNum());
            resultTool.setMessage(ReturnMessage.INVALID_INTERVIEW_LINK.getCodeMessage());
            return resultTool;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = formatter.parse((String) map.get("startTime"));
        Date endTime = formatter.parse((String) map.get("endTime"));
        String termName = termService.getTermVerified((String) map.get("termName"));
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, (String) map.get("className"));
        Integer classId = classList.get(0).getId();
        String interviewLink = Status.TENCENT_MEETING_URL.getLabel() + map.get("interviewLink");
        String status = classRepository.findStatusByUsernameAndClassId((String) map.get("studentName"), classId);
        classRepository.updateStatusToArrangeInterview(classId, (String) map.get("studentName"), interviewLink
                , startTime, endTime);

        //send status update notify
        String description = null;
        if (Status.ENROLLED.getLabel().equals(status)){
            description = "??????????????? " + termName + " - " + map.get("className") + " ???????????????????????????????????? ???????????? ???????????????????????????";
        }else if (Status.INTERVIEWING.getLabel().equals(status)){
            description = "???????????????????????? " + termName + " - " + map.get("className") + " ????????????????????????????????? ???????????? ????????????????????????";
        }
        List<User> userList = getStudentAndTeacherAccountByUsername((String) map.get("studentName"));
        notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                , userList, description);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @PostMapping("/updateStatusToHired")
    public ResultTool updateStatusToHired(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String username = (String) map.get("studentName");
        String termName = termService.getTermVerified((String) map.get("termName"));
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, (String) map.get("className"));
        Class aClass = classList.get(0);
        if (aClass.getUser() != null){
            resultTool.setCode(ReturnMessage.CLASS_ASSIGNED_TEACHER.getCodeNum());
            resultTool.setMessage(ReturnMessage.CLASS_ASSIGNED_TEACHER.getCodeMessage());
            return resultTool;
        }
        Integer classId = classList.get(0).getId();
        classRepository.updateStatusToHired(classId, username, new Date());
        User teacher = userRepository.findByNameAndRole(username, Role.teacher.name());
        if (teacher == null){
            //create teacher account and send welcome notify
            User teacherAccount = new User();
            User userAccount = userRepository.findByNameAndRole(username, Role.student.name());
            teacherAccount.setPwd(Status.DEFAULT_PASSWORD.getLabel());
            teacherAccount.setName(username);
            teacherAccount.setStuId(userAccount.getStuId());
            teacherAccount.setClassName(userAccount.getClassName());
            teacherAccount.setRole(Role.teacher.name());
            teacherAccount.setImgUrl(Status.DEFAULT_IMG.getLabel());
            userRepository.save(teacherAccount);

            //welcome notify
            String description = "????????????????????????";
            List<User> userList = new ArrayList<>();
            User receiver = userRepository.findByNameAndRole(username, Role.teacher.name());
            userList.add(receiver);
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.SYSTEM_NOTIFY.getLabel()
                    , userList, description);

            //new teacher account information notify
            String description1 = "??????????????????????????? ??????????????????" + teacherAccount.getStuId() + ", ???????????????111111";
            List<User> userList1 = new ArrayList<>();
            User receiver1 = userRepository.findByNameAndRole(username, Role.student.name());
            userList1.add(receiver1);
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.SYSTEM_NOTIFY.getLabel()
                    , userList1, description1);
        }
        //set teacher for class
        aClass.setUser(userRepository.findByNameAndRole(username, Role.teacher.name()));
        aClass.setStatus(Status.ENROLL_TEACHER_FINISH.getLabel());
        classRepository.save(aClass);

        //send hired notify to student and teacher account
        String description = "?????????????????? " + termName + " - " + map.get("className") + " ??????????????????????????????????????????";
        List<User> userList = getStudentAndTeacherAccountByUsername(username);
        notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                , userList, description);

        //send hired notify to admin account
        List<User> adminList = new ArrayList<>();
        User admin = userRepository.findByRole(Role.admin.name()).get(0);
        adminList.add(admin);
        String description2 = username + "?????????????????????offer????????? " + termName + " - " + map.get("className") + " ????????????";
        notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                , adminList, description2);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @SaCheckRole("admin")
    @PostMapping("/updateStatusToPassed")
    public ResultTool updateStatusToPassed(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String username = (String) map.get("studentName");
        String termName = termService.getTermVerified((String) map.get("termName"));
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, (String) map.get("className"));
        Class aClass = classList.get(0);
        //check whether status can be passed
        String status = classRepository.findTeachEnrollByClassIdAndStatus(aClass.getId(), Status.PASSED.getLabel());
        if (!StringUtils.isEmpty(status)){
            resultTool.setCode(ReturnMessage.PASSED_EXIST.getCodeNum());
            resultTool.setMessage(ReturnMessage.PASSED_EXIST.getCodeMessage());
            return resultTool;
        }

        //update status to passed
        classRepository.updateStatusToPassed(aClass.getId(), username, new Date());

        //send passed notify
        String description = "?????????????????? " + termName + " - " + map.get("className") + " ????????????????????????????????? ???????????? ??????offer?????????";
        List<User> userList = getStudentAndTeacherAccountByUsername(username);
        notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                , userList, description);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @PostMapping("/updateStatusToInterrupted")
    public ResultTool updateStatusToInterrupted(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String reason = (String) map.get("reason");
        if (StringUtils.isEmpty(reason)){
            resultTool.setCode(ReturnMessage.NULL_REFUSE_REASON.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_REFUSE_REASON.getCodeMessage());
            return resultTool;
        }
        String username = (String) map.get("studentName");
        String termName = termService.getTermVerified((String) map.get("termName"));
        List<Class> classList = classRepository.findByTermNameAndClassName(termName, (String) map.get("className"));
        Class aClass = classList.get(0);
        classRepository.updateStatusToInterrupted(reason, aClass.getId(), username, new Date());

        if ((Boolean) map.get("fromAdmin")){
            //send refuse notify to student and teacher account
            List<User> userList = getStudentAndTeacherAccountByUsername(username);
            String description2 = "?????? " + termName + " - " + map.get("className") + " ????????????????????????????????????: " + reason;
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                    , userList, description2);
        }else {
            //send refuse notify to admin account
            List<User> adminList = new ArrayList<>();
            User admin = userRepository.findByRole(Role.admin.name()).get(0);
            adminList.add(admin);
            String description = username + "?????????????????????offer: " + termName + " - " + map.get("className") + " ?????????????????? " + reason;
            notifyService.adminSendNotifyToSpecificUser(NotifyTheme.INTERVIEW_STATUS_CHANGE.getLabel()
                    , adminList, description);
        }

        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    private List<User> getStudentAndTeacherAccountByUsername(String username){
        List<User> userList = new ArrayList<>();
        User studentAccount = userRepository.findByNameAndRole(username, Role.student.name());
        userList.add(studentAccount);
        User teacherAccount = userRepository.findByNameAndRole(username, Role.teacher.name());
        if (teacherAccount != null){
            userList.add(teacherAccount);
        }
        return userList;
    }
}
