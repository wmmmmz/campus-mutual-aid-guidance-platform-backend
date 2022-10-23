package com.wmz.campusplatform.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;


    @PostMapping("/login")
    public ResultTool login(String username, String pwd, String role){
        ResultTool resultTool = new ResultTool();
        User user = userRepository.findByStuIdAndPwdAndRole(username, pwd, role);
        if (user != null){
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            StpUtil.login(user.getId());
        }else {
            resultTool.setCode(ReturnMessage.WRONG_USERNAME_OR_PASSWORD.getCodeNum());
            resultTool.setMessage(ReturnMessage.WRONG_USERNAME_OR_PASSWORD.getCodeMessage());
        }
        return resultTool;
    }


    @PostMapping("/loginOut")
    public ResultTool loginOut(){
        ResultTool resultTool = new ResultTool();
        resultTool.setData(StpUtil.getLoginId());
        StpUtil.logout();
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }


    @GetMapping("/isLogin")
    public ResultTool isLogin(){
        ResultTool resultTool = new ResultTool();
        resultTool.setData(StpUtil.isLogin());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }
}
