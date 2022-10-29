package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.UserRepository;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserController {

    @Autowired
    private UserRepository userRepository;

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

}
