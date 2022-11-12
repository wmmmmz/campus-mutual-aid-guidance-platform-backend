package com.wmz.campusplatform.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.wmz.campusplatform.convert.UserDetailsConvert;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsConvert userDetailsConvert;

    @Autowired
    private MongoDBService mongoDBService;

    @PostMapping("/login")
    public ResultTool login(@RequestBody Map<String, String> map){
        String username = map.get("username");
        String pwd = map.get("pwd");
        String role = map.get("role");
        ResultTool resultTool = new ResultTool();
        User user = userRepository.findByStuIdAndPwdAndRole(username, DigestUtils.md5DigestAsHex(pwd.getBytes()), role);
        if (user != null){
            StpUtil.login(user.getId());
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
            byte[] imgFile;
            String imgPre = null;
            List<Img> imgListByImgUrl = mongoDBService.getImgListByImgUrl(user.getImgUrl());
            if(imgListByImgUrl.size() == 0){
                imgFile = mongoDBService.getImgListByImgUrl("default").get(0).getImgFile();
            }else{
                imgFile = imgListByImgUrl.get(0).getImgFile();
                imgPre = imgListByImgUrl.get(0).getImgPre();
            }
            resultTool.setData(userDetailsConvert.userConvert(user, StpUtil.getTokenValue(), imgPre, Base64.getEncoder().encodeToString(imgFile)));
        }else if (userRepository.findByStuIdAndPwd(username, DigestUtils.md5DigestAsHex(pwd.getBytes())) == null){
            resultTool.setCode(ReturnMessage.WRONG_USERNAME_OR_PASSWORD.getCodeNum());
            resultTool.setMessage(ReturnMessage.WRONG_USERNAME_OR_PASSWORD.getCodeMessage());
        }else {
            resultTool.setCode(ReturnMessage.WRONG_IDENTITY.getCodeNum());
            resultTool.setMessage(ReturnMessage.WRONG_IDENTITY.getCodeMessage());
        }
        return resultTool;
    }


    @PostMapping("/loginOut")
    public ResultTool loginOut(){
        ResultTool resultTool = new ResultTool();
        String tokenValue = StpUtil.getTokenValue();
        StpUtil.logoutByTokenValue(tokenValue);
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
