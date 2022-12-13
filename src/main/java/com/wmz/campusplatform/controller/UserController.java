package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.NotifyAnnounceWithStatus;
import com.wmz.campusplatform.details.UserDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.NotifyAnnounceRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.MongoDBService;
import com.wmz.campusplatform.service.NotifyService;
import com.wmz.campusplatform.service.PageService;
import com.wmz.campusplatform.utils.MongoAutoIdUtil;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotifyAnnounceRepository notifyAnnounceRepository;

    @Autowired
    private MongoAutoIdUtil mongoAutoIdUtil;

    @Autowired
    private PageService pageService;

    @Autowired
    private NotifyService notifyService;

    @PostMapping("/updateOrSave")
    public ResultTool updateOrSaveUser(@RequestBody User user){
        ResultTool resultTool = new ResultTool();
        User user1 = userRepository.findByStuIdAndRole(user.getStuId(), user.getRole());
        if (user1 == null){
            log.error("用户不存在");
            resultTool.setCode(ReturnMessage.NO_USER.getCodeNum());
            resultTool.setMessage(ReturnMessage.NO_USER.getCodeMessage());
        }else{
            user1.setDescription(user.getDescription());
            user1.setWx(user.getWx());
            user1.setTel(user.getTel());
            userRepository.save(user1);
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            resultTool.setData(user1);
        }
        return resultTool;
    }

    @PostMapping("/changePassword")
    public ResultTool changePassword(@RequestBody User user){
        ResultTool resultTool = new ResultTool();
        User user1 = userRepository.findByStuIdAndRole(user.getStuId(), user.getRole());
        if (user1 == null){
            log.error("用户不存在");
            resultTool.setCode(ReturnMessage.NO_USER.getCodeNum());
            resultTool.setMessage(ReturnMessage.NO_USER.getCodeMessage());
        }else{
            user1.setPwd(DigestUtils.md5DigestAsHex(user.getPwd().getBytes()));
            userRepository.save(user1);
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            resultTool.setData(user1);
        }
        return resultTool;
    }

    @PostMapping("/checkPassword")
    public ResultTool checkPassword(@RequestBody User user){
        ResultTool resultTool = new ResultTool();
        User user1 = userRepository.findByStuIdAndPwdAndRole(user.getStuId(), DigestUtils.md5DigestAsHex(user.getPwd().getBytes()), user.getRole());
        if (user1 == null){
            log.error("用户不存在");
            resultTool.setCode(ReturnMessage.NO_USER.getCodeNum());
            resultTool.setMessage(ReturnMessage.NO_USER.getCodeMessage());
        }else{
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            resultTool.setData(user1);
        }
        return resultTool;
    }

    @PostMapping("/savePhoto")
    public ResultTool savePhoto(@RequestBody UserDetails userDetails){
        ResultTool resultTool = new ResultTool();
        User user1 = userRepository.findByStuIdAndRole(userDetails.getStuId(), userDetails.getRole());
        if (user1 == null){
            log.error("用户不存在");
            resultTool.setCode(ReturnMessage.NO_USER.getCodeNum());
            resultTool.setMessage(ReturnMessage.NO_USER.getCodeMessage());
        }else{
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            String imgUrl = userDetails.getStuId() + "_" + userDetails.getRole();
            user1.setImgUrl(imgUrl);
            userRepository.updateImgUrl(user1.getImgUrl(), user1.getStuId(), user1.getRole());
            String[] split = userDetails.getImgBase64().split(",");
            List<Img> img = mongoDBService.getImgListByImgUrl(imgUrl);
            if (img.size() != 0){
                for (Img img1 : img) {
                    mongoTemplate.remove(img1);
                }
            }
            mongoDBHelper.save(new Img(mongoAutoIdUtil.getNextSequence("seq_img"), imgUrl, split[0], Base64.getDecoder().decode(split[1])));
            resultTool.setData(userDetails);
        }
        return resultTool;
    }

    @GetMapping("/getNotifyList")
    public ResultTool getNotifyList(@RequestParam String stuId, String role,
                                    @RequestParam(required = false) Integer pageIndex,
                                    @RequestParam(required = false) Integer pageSize){
        ResultTool resultTool = new ResultTool();
        User user = userRepository.findByStuIdAndRole(stuId, role);
        List<Map<String, Object>> notifyOfUserWithStatus = null;
        Integer offSet = pageSize * (pageIndex - 1);
        if (Role.admin.name().equals(role)){
            notifyOfUserWithStatus = notifyAnnounceRepository.findNotifyOfAllAdminWithStatus();
        }else {
            notifyOfUserWithStatus = notifyAnnounceRepository.findNotifyOfUserWithStatus(user.getId());
        }
        List<NotifyAnnounceWithStatus> notifyAnnounceWithStatuses = new ArrayList<>();
        for (Map<String, Object> ofUserWithStatus : notifyOfUserWithStatus) {
            NotifyAnnounceWithStatus notifyAnnounceWithStatus = new NotifyAnnounceWithStatus();
            for(Map.Entry<String, Object> m : ofUserWithStatus.entrySet()){
                switch (m.getKey()){
                    case "status":
                        notifyAnnounceWithStatus.setStatus((String) m.getValue());
                        break;
                    case "title":
                        notifyAnnounceWithStatus.setTitle((String) m.getValue());
                        break;
                    case "content":
                        notifyAnnounceWithStatus.setContent((String) m.getValue());
                        break;
                    case "createTime":
                        notifyAnnounceWithStatus.setCreateTime((Date) m.getValue());
                }
            }
            notifyAnnounceWithStatuses.add(notifyAnnounceWithStatus);
        }
        Map<String, List<NotifyAnnounceWithStatus>> collect = notifyAnnounceWithStatuses.stream()
                //sort by create time DESC
                .sorted(Comparator.comparing(NotifyAnnounceWithStatus::getCreateTime).reversed())
                //collect by status UNREADED, READ, RECYCLE
                .collect(Collectors.groupingBy(NotifyAnnounceWithStatus::getStatus));
         Map<String, List<NotifyAnnounceWithStatus>> dataList = new HashMap<>();
        Integer UnreadTotalSize = collect.getOrDefault(Status.UNREADED.name(), new ArrayList<>()).size();
        Integer readTotalSize = collect.getOrDefault(Status.READED.name(), new ArrayList<>()).size();
        Integer recycleTotalSize = collect.getOrDefault(Status.RECYCLE.name(), new ArrayList<>()).size();
        for(Map.Entry<String, List<NotifyAnnounceWithStatus>> map : collect.entrySet()){
            if (offSet < Math.min(offSet + pageSize, map.getValue().size())){
                dataList.put(map.getKey(), map.getValue().subList(offSet, Math.min(offSet + pageSize, map.getValue().size())));
            }else {
                dataList.put(map.getKey(), new ArrayList<>());
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dataList", dataList);
        result.put("UnreadTotalSize", UnreadTotalSize);
        result.put("readTotalSize", readTotalSize);
        result.put("recycleTotalSize", recycleTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(result);
        return resultTool;
    }

    @GetMapping("/getAdminDataList")
    public ResultTool getAdminDataList(@RequestParam String query,
                                       @RequestParam(required = false) Integer pageIndex,
                                       @RequestParam(required = false) Integer pageSize){
        ResultTool resultTool = new ResultTool();
        List<User> adminList = userRepository.findByRoleAndPage(Role.admin.name(), PageRequest.of(pageIndex - 1, pageSize));
        List<User> adminCollection = filterUserDetailsByQuery(adminList, query);
        Integer adminTotalSize = filterUserDetailsByQuery(userRepository.findByRole(Role.admin.name()), query).size();
        Map<String, Object> pageData = pageService.getPageData(adminCollection, adminTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
        return resultTool;
    }

    /**
     * get students don't have admin account
     * @return
     */
    @GetMapping("/getStudentList")
    public ResultTool getStudentList(){
        ResultTool resultTool = new ResultTool();
        List<User> studentList = userRepository.findByRole(Role.student.name());
        List<User> studentWithoutAdminAccount = studentList.stream()
                .filter(student -> userRepository.findByStuIdAndRole(student.getStuId(), Role.admin.name()) == null)
                .collect(Collectors.toList());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(studentWithoutAdminAccount);
        return resultTool;
    }

    @PostMapping("/saveAdminList")
    public ResultTool saveAdminList(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        List<String> stuIdList = (List<String>) map.get("stuIdList");
        for (String stuId : stuIdList) {
            createAdminAccountBasedOnStudentAccount(stuId);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    private List<User> filterUserDetailsByQuery(List<User> userList, String query){
        return userList.stream().filter(user -> !StringUtils.isEmpty(user.getName()) && user.getName().contains(query) ||
                !StringUtils.isEmpty(user.getClassName()) && user.getClassName().contains(query) ||
                !StringUtils.isEmpty(user.getStuId()) && user.getStuId().contains(query) ||
                !StringUtils.isEmpty(user.getTel()) && user.getTel().contains(query) ||
                !StringUtils.isEmpty(user.getWx()) && user.getWx().contains(query)).collect(Collectors.toList());
    }

    private void createAdminAccountBasedOnStudentAccount(String stuId){
        User student = userRepository.findByStuIdAndRole(stuId, Role.student.name());
        User admin = new User();
        admin.setName(student.getName());
        admin.setPwd(Status.DEFAULT_PASSWORD.getLabel());
        admin.setClassName(student.getClassName());
        admin.setRole(Role.admin.name());
        admin.setStuId(student.getStuId());
        admin.setTel(student.getTel());
        admin.setWx(student.getWx());
        admin.setImgUrl(Status.DEFAULT_IMG.getLabel());
        userRepository.save(admin);
    }

}
