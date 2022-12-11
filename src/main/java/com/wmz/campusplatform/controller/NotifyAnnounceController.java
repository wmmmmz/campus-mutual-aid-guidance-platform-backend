package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.NotifyAnnounceDetailConvert;
import com.wmz.campusplatform.details.NotifyAnnounceDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.NotifyAnnounceRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.PageService;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.text.ParseException;
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

    @Autowired
    private PageService pageService;

    /**
     * admin管理员手动发布新通知
     * @param notifyAnnounceDetails
     * @return
     */
    @PostMapping("/saveNotify")
    public ResultTool saveNotify(@RequestBody NotifyAnnounceDetails notifyAnnounceDetails){
        ResultTool resultTool = new ResultTool();
        if (StringUtils.isEmpty(notifyAnnounceDetails.getTitle())){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_TITLE.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_TITLE.getCodeMessage());
        }else if (StringUtils.isEmpty(notifyAnnounceDetails.getContent())){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_CONTENT.getCodeMessage());
        }else if (notifyAnnounceDetails.getReceiverRole().size() == 0){
            resultTool.setCode(ReturnMessage.NULL_NOTIFY_RECEIVER.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_NOTIFY_RECEIVER.getCodeMessage());
        } else {
            NotifyAnnounce notifyAnnounce = notifyAnnounceDetailConvert.notifyAnnounceDetailConvert(notifyAnnounceDetails);
            notifyAnnounce.setAuto(false);
            notifyAnnounceRepository.save(notifyAnnounce);
            resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
            resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        }
        return resultTool;
    }

    /**
     * admin管理员查看手动发布的通知
     * @param stuId
     * @param role
     * @param query
     * @return
     */
    @GetMapping("/getNotifyISent")
    public ResultTool getNotifyISent(@RequestParam String stuId,
                                     @RequestParam String role,
                                     @RequestParam(required = false) String query,
                                     @RequestParam(required = false) Integer pageIndex,
                                     @RequestParam(required = false) Integer pageSize){
        ResultTool resultTool = new ResultTool();
        User sender = null;
        Integer offSet = pageSize * (pageIndex - 1);
        List<NotifyAnnounceDetails> notifyAnnounceDetailsList = new ArrayList<>();
        List<Map<String, Object>> notifyAnnounceList = null;
//        if (Role.admin.name().equals(role)){
        Integer notifyTotalSize = notifyAnnounceRepository.getNotifyAnnounceAdminSendTotalSize(query, false);
        notifyAnnounceList = notifyAnnounceRepository.findByAllAdminSenderAndQueryByPage(query, false, pageSize, offSet);
//        }else{
//            sender = userRepository.findByStuIdAndRole(stuId, role);
//            notifyAnnounceList = notifyAnnounceRepository.findBySenderAndQuery(sender.getId(), query, false);
//        }
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
                    case "name":
                        notifyAnnounceDetails.setSenderName((String) m.getValue());
                        break;
                    case "class_name":
                        notifyAnnounceDetails.setSenderClassName((String) m.getValue());
                        break;
                    case "wx":
                        notifyAnnounceDetails.setSenderWx((String) m.getValue());
                        break;
                    case "tel":
                        notifyAnnounceDetails.setSenderTel((String) m.getValue());
                        break;
                }
            }
            notifyAnnounceDetailsList.add(notifyAnnounceDetails);
        }
        Map<String, Object> pageData = pageService.getPageData(notifyAnnounceDetailsList, notifyTotalSize);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(pageData);
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

    @PostMapping("/changeStatus")
    public ResultTool changeStatus(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        Integer notifyId = null;
        if (map.containsKey("title")){
            String title = (String) map.get("title");
            String content = (String) map.get("content");
            String createTimeString = (String) map.get("createTime");
            Date createTime;
            SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
            try {
                if (!StringUtils.isEmpty(createTimeString))
                    createTime =  formatter.parse(createTimeString);
                else
                    createTime = null;
            } catch (ParseException e) {
                log.error("string to date error");
                throw new RuntimeException(e);
            }
            List<NotifyAnnounce> notifyList = notifyAnnounceRepository.findNotifyAnnounceByTitleAndContentAndCreateTime(title, content, createTime);
            notifyId = notifyList.get(0).getId();
        }
        String status = (String) map.get("status");
        String stuId = (String) map.get("stuId");
        String role = (String) map.get("role");
        Boolean readAll = (Boolean) map.get("readAll");
        if (Role.admin.name().equals(role)){
            if (readAll){
                notifyAnnounceRepository.adminChangeAllStatus(status);
            }else {
                notifyAnnounceRepository.adminChangeStatus(notifyId, status);
            }
        }else{
            User user = userRepository.findByStuIdAndRole(stuId, role);
            Integer userId = user.getId();
            if (readAll){
                notifyAnnounceRepository.changeAllStatus(userId, status);
            }else {
                notifyAnnounceRepository.changeStatus(notifyId, userId, status);
            }
        }

        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @PostMapping("/clearRecycle")
    public ResultTool clearRecycle(@RequestBody Map<String, String> map){
        ResultTool resultTool = new ResultTool();
        String stuId = map.get("stuId");
        String role = map.get("role");
        if (Role.admin.name().equals(role)){
            notifyAnnounceRepository.adminDeleteRecycleNotify();
        }else{
            User user = userRepository.findByStuIdAndRole(stuId, role);
            Integer userId = user.getId();
            notifyAnnounceRepository.deleteRecycleNotify(userId);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }


}
