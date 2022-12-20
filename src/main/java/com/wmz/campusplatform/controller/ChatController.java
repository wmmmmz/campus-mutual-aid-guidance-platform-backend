package com.wmz.campusplatform.controller;

import com.wmz.campusplatform.convert.ConversationDetailsConvert;
import com.wmz.campusplatform.convert.MessageDetailsConvert;
import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.details.MessageDetails;
import com.wmz.campusplatform.details.NotifyAnnounceWithStatus;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ConversationRepository;
import com.wmz.campusplatform.repository.MessageRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.ChatService;
import com.wmz.campusplatform.service.ConversationService;
import com.wmz.campusplatform.service.MongoDBService;
import com.wmz.campusplatform.utils.StringUtils;
import io.swagger.models.auth.In;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ConversationDetailsConvert conversationDetailsConvert;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageDetailsConvert messageDetailsConvert;

    @Autowired
    private ChatService chatService;

    @PostMapping("/createConversation")
    public ResultTool createConversation(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String stuId = (String) map.get("stuId");
        String myStuId = (String) map.get("myStuId");
        if (StringUtils.isEmpty(stuId)){
            resultTool.setCode(ReturnMessage.NULL_STUDENT_LIST.getCodeNum());
            resultTool.setMessage(ReturnMessage.NULL_STUDENT_LIST.getCodeMessage());
            return resultTool;
        }
        if (conversationService.conversationExist(stuId, myStuId)){
            resultTool.setCode(ReturnMessage.CONVERSATION_EXIST.getCodeNum());
            resultTool.setMessage(ReturnMessage.CONVERSATION_EXIST.getCodeMessage());
            return resultTool;
        }
        Conversation conversation = new Conversation();
        conversation.setAvatarUrl(null);
        conversation.setName(stuId + "_" + myStuId);
        List<User> userList = new ArrayList<>();
        if (!stuId.equals(myStuId)){
            userList.add(userRepository.findByStuIdAndRole(stuId, Role.student.name()));
        }
        userList.add(userRepository.findByStuIdAndRole(myStuId, Role.student.name()));
        conversation.setUserList(userList);
        conversationRepository.save(conversation);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    @GetMapping("/getMyConversation")
    public ResultTool getMyConversation(@RequestParam String stuId){
        ResultTool resultTool = new ResultTool();
        List<ConversationDetails> conversationDetailsList = chatService.getMyConversation(stuId);
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(conversationDetailsList);
        return resultTool;
    }

    @GetMapping("/getMessageList")
    public ResultTool getMessageList(@RequestParam String myStuId,
                                     @RequestParam String stuId){
        ResultTool resultTool = new ResultTool();
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Message> messageList = messageRepository.findMessageByConversationNameList(conversationNameList);
        List<MessageDetails> messageDetailsList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDetails messageDetails = messageDetailsConvert.messageDetailConvert(message, myStuId);
            messageDetailsList.add(messageDetails);
        }
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        resultTool.setData(messageDetailsList);
        return resultTool;

    }

//    @PostMapping("/saveMessage")
//    public ResultTool saveMessage(@RequestBody Map<String, Object> map){
//        ResultTool resultTool = new ResultTool();
//        String stuId = (String) map.get("stuId");
//        String myStuId = (String) map.get("myStuId");
//        String content = (String) map.get("content");
//        if (StringUtils.isEmpty(content)){
//            resultTool.setCode(ReturnMessage.NULL_CONTENT.getCodeNum());
//            resultTool.setMessage(ReturnMessage.NULL_CONTENT.getCodeMessage());
//            return resultTool;
//        }
//        Conversation conversation = getConversationByStuIdList(myStuId, stuId);
//        User user = userRepository.findByStuIdAndRole(myStuId, Role.student.name());
//        Message message = new Message();
//        message.setContent(content);
//        message.setUser(user);
//        message.setConversation(conversation);
//        message.setPublishTime(new Date());
//        messageRepository.save(message);
//        //add unreadCnt
//        if (!stuId.equals(myStuId)){
//            conversationRepository.updateUnreadCntByConversationIdAndUserId(conversation.getId()
//                    , userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId());
//        }
//
//        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
//        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
//        return resultTool;
//    }

    @PostMapping("/clearUnreadCnt")
    public ResultTool clearUnreadCnt(@RequestBody Map<String, Object> map){
        ResultTool resultTool = new ResultTool();
        String stuId = (String) map.get("stuId");
        String myStuId = (String) map.get("myStuId");
        Conversation conversation = getConversationByStuIdList(myStuId, stuId);
        conversationRepository.clearUnreadCntByConversationIdAndUserId(conversation.getId()
                , userRepository.findByStuIdAndRole(myStuId, Role.student.name()).getId());
        resultTool.setCode(ReturnMessage.SUCCESS_CODE.getCodeNum());
        resultTool.setMessage(ReturnMessage.SUCCESS_CODE.getCodeMessage());
        return resultTool;
    }

    private Conversation getConversationByStuIdList(String myStuId, String stuId) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Conversation> conversationList = conversationRepository.findConversationByNameIn(conversationNameList);
        return conversationList.get(0);
    }

}
