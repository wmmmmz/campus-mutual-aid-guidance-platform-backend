package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.details.NotifyAnnounceWithStatus;
import com.wmz.campusplatform.details.UserDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.NotifyAnnounceRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.MongoDBService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
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
            mongoDBHelper.save(new Img(mongoDBHelper.findAll(Img.class).size() + 1, imgUrl, Base64.getDecoder().decode(split[1])));
            resultTool.setData(userDetails);
        }
        return resultTool;
    }

    @GetMapping("/getNotifyList")
    public ResultTool getNotifyList(@RequestParam String stuId, String role){
        ResultTool resultTool = new ResultTool();
        User user = userRepository.findByStuIdAndRole(stuId, role);
        List<Map<String, Object>> notifyOfUserWithStatus = notifyAnnounceRepository.findNotifyOfUserWithStatus(user.getId());
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
        Map<String, List<NotifyAnnounceWithStatus>> collect = notifyAnnounceWithStatuses.stream().collect(Collectors.groupingBy(data -> data.getStatus()));
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(collect);
        return resultTool;
    }
}
