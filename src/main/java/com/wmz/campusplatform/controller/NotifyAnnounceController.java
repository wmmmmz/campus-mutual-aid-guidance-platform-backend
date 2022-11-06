package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.NotifyAnnounceDetailConvert;
import com.wmz.campusplatform.details.NotifyAnnounceDetails;
import com.wmz.campusplatform.pojo.NotifyAnnounce;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.NotifyAnnounceRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifyAnnounce")
@Log4j2
public class NotifyAnnounceController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotifyAnnounceDetailConvert notifyAnnounceDetailConvert;

    @Autowired
    private NotifyAnnounceRepository notifyAnnounceRepository;

    @PostMapping("/saveNotify")
    public ResultTool saveNotify(@RequestBody NotifyAnnounceDetails notifyAnnounceDetails){
        ResultTool resultTool = new ResultTool();
        if (StringUtils.isEmpty(notifyAnnounceDetails.getTitle())){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_TITLE.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_TITLE.getCodeMessage());
        }else if (StringUtils.isEmpty(notifyAnnounceDetails.getContent())){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeMessage());
        }else if (notifyAnnounceDetails.getReceiverRole() == null){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeMessage());
        } else {
            NotifyAnnounce notifyAnnounce = notifyAnnounceDetailConvert.notifyAnnounceDetailConvert(notifyAnnounceDetails);
            notifyAnnounceRepository.save(notifyAnnounce);
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }
        return resultTool;
    }

    @GetMapping("/getNotifyISent")
    public ResultTool getNotifyISent(@RequestParam String stuId,
                                     @RequestParam String role,
                                     @RequestParam(required = false) String query){
        ResultTool resultTool = new ResultTool();
        User sender = userRepository.findByStuIdAndRole(stuId, role);
        List<NotifyAnnounceDetails> notifyAnnounceDetailsList = new ArrayList<>();
        List<Map<String, Object>> notifyAnnounceList = notifyAnnounceRepository.findBySenderAndQuery(sender.getId(), query);
        for (Map<String, Object> notifyAnnounce : notifyAnnounceList) {
            NotifyAnnounceDetails notifyAnnounceDetails = new NotifyAnnounceDetails();
            for(Map.Entry<String, Object> m : notifyAnnounce.entrySet()){
                switch (m.getKey()){
                    case "title":
                        notifyAnnounceDetails.setTitle((String) m.getValue());
                        break;
                    case "content":
                        notifyAnnounceDetails.setContent((String)m.getValue());
                        break;
                    case "createTime":
                        notifyAnnounceDetails.setCreateTime((Date) m.getValue());
                        break;
                    case "unreadCnt":
                        notifyAnnounceDetails.setUnreadCnt((BigInteger) m.getValue());
                        break;
                }
            }
            notifyAnnounceDetailsList.add(notifyAnnounceDetails);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(notifyAnnounceDetailsList);
        return resultTool;
    }

    @PostMapping("/deleteNotify")
    public ResultTool deleteNotify(@RequestBody NotifyAnnounceDetails notifyAnnounceDetails){
        ResultTool resultTool = new ResultTool();
        notifyAnnounceRepository.deleteByTitleAndContentAndCreateTime(notifyAnnounceDetails.getTitle(),
                notifyAnnounceDetails.getContent(), notifyAnnounceDetails.getCreateTime());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }


}
