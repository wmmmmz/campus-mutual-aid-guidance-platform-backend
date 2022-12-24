package com.wmz.campusplatform.service;

import com.wmz.campusplatform.convert.ConversationDetailsConvert;
import com.wmz.campusplatform.convert.MessageDetailsConvert;
import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.details.MessageDetails;
import com.wmz.campusplatform.handler.MongoDBHelper;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ConversationRepository;
import com.wmz.campusplatform.repository.MessageRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.utils.BooleanUtils;
import com.wmz.campusplatform.utils.DateUtil;
import com.wmz.campusplatform.utils.MongoAutoIdUtil;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ChatServiceImpl implements ChatService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationDetailsConvert conversationDetailsConvert;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageDetailsConvert messageDetailsConvert;

    @Autowired
    private MongoDBHelper mongoDBHelper;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private MongoAutoIdUtil mongoAutoIdUtil;

    @Override
    @Transactional
    public List<ConversationDetails> getMyConversation(String stuId) {
        User user = userRepository.findByStuIdAndRole(stuId, Role.student.name());
        List<Conversation> conversationList = user.getConversationList();
        List<ConversationDetails> conversationDetailsList = new ArrayList<>();
        for (Conversation conversation : conversationList) {
            ConversationDetails conversationDetails = conversationDetailsConvert.conversationDetailConvert(stuId, conversation);
            conversationDetailsList.add(conversationDetails);
        }
        conversationDetailsList =  conversationDetailsList.stream()
                .sorted(Comparator.comparing(ConversationDetails::getLatestMessageTime).reversed())
                .collect(Collectors.toList());

        for (ConversationDetails conversationDetails : conversationDetailsList) {
            String latestMessageTime = conversationDetails.getLatestMessageTime();
            if (StringUtils.isEmpty(latestMessageTime)){
                continue;
            }
            conversationDetails.setLatestMessageTime(getTimeByDate(latestMessageTime));
        }
        return conversationDetailsList;
    }

    @Override
    public void saveMessage(String stuId, String myStuId, String content, Boolean isFile, List<String> tempFilePath, List<String> suffixName, List<String> fileName) {
        Conversation conversation = getConversationByStuIdList(myStuId, stuId);
        User user = userRepository.findByStuIdAndRole(myStuId, Role.student.name());
        //save normal message    without img content must be not null
        if (!StringUtils.isEmpty(content)){
            saveMessageInDBAndAddUnreadCnt(myStuId, stuId, conversation, user, content, false, false);
        }
        //save file
        if (!BooleanUtils.isFalse(isFile) && tempFilePath.size() != 0){
            for (int i = 1; i < tempFilePath.size(); i++) {
                String name = fileName.get(i) + "." + fileUploadService.generateUUID();
                File file = new File(tempFilePath.get(i));
                String filePre = fileUploadService.getBase64PrefixByFileSuffix(suffixName.get(i).toLowerCase());
                try {
                    mongoDBHelper.save(new ChatBoxFile(mongoAutoIdUtil.getNextSequence("seq_chatBoxFile")
                            , name, filePre, fileUploadService.fileToByte(file)));
                } catch (IOException e) {
                    log.error("save chat box file in mongoDB fail");
                    throw new RuntimeException(e);
                }
                //check is img or file
                Boolean isImg = isImg(suffixName.get(i));
                if (isImg){
                    saveMessageInDBAndAddUnreadCnt(myStuId, stuId, conversation, user, name, false, true);
                }else {
                    saveMessageInDBAndAddUnreadCnt(myStuId, stuId, conversation, user, name, true, false);
                }

            }
        }
       
    }

    @Override
    public List<MessageDetails> getMessageList(String myStuId, String stuId, Integer startIndex, Integer pageSize) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        Integer totalCnt = messageRepository.getMessageTotalCntByConversationNameList(conversationNameList);
        if (startIndex + pageSize > totalCnt){
            pageSize = totalCnt + startIndex;
        }
        List<Message> messageList = messageRepository.findMessageByConversationNameList(conversationNameList, pageSize, startIndex);
        List<MessageDetails> messageDetailsList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDetails messageDetails = messageDetailsConvert.messageDetailConvert(message, myStuId);
            messageDetailsList.add(messageDetails);
        }
        return messageDetailsList;
    }

    @Override
    public String getTimeByDate(String time) {
        SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatYear = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatMonth = new SimpleDateFormat("MM-dd");
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = fmt.parse(time);
            if (DateUtil.isToday(date)){
                return formatToday.format(date);
            }else if(DateUtil.isThisYear(date)){
                return formatMonth.format(date);
            }else {
               return formatYear.format(date);
            }
        } catch (ParseException e) {
            log.error("time parse error");
            throw new RuntimeException(e);
        }
    }

    private Conversation getConversationByStuIdList(String myStuId, String stuId) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Conversation> conversationList = conversationRepository.findConversationByNameIn(conversationNameList);
        return conversationList.get(0);
    }

//    public static boolean isThisTime(Date date, String pattern) {
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//        String param = sdf.format(date);//参数时间
//        String now = sdf.format(new Date());//当前时间
//        if (param.equals(now)) {
//            return true;
//        }
//        return false;
//    }

    @Transactional
    public void saveMessageInDBAndAddUnreadCnt(String myStuId, String stuId, Conversation conversation, User user
            , String content, Boolean isFile, Boolean isImg){
        Message message = new Message();
        message.setContent(content);
        message.setUser(user);
        message.setConversation(conversation);
        message.setPublishTime(new Date());
        message.setFile(isFile);
        message.setImg(isImg);
        messageRepository.save(message);
        //add unreadCnt
        if (!stuId.equals(myStuId)){
            conversationRepository.updateUnreadCntByConversationIdAndUserId(conversation.getId()
                    , userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId());
        }
    }

//    public static boolean isToday(Date date) {
//        return isThisTime(date, "yyyy-MM-dd");
//    }
//
//    //判断选择的日期是否是本年
//    public static boolean isThisYear(Date time) {
//        return isThisTime(time, "yyyy");
//    }

    private Boolean isImg(String suffixName){
        if (".jpg".equalsIgnoreCase(suffixName) || ".jpeg".equalsIgnoreCase(suffixName)
                || ".png".equalsIgnoreCase(suffixName) || ".gif".equalsIgnoreCase(suffixName)
                || ".svg".equalsIgnoreCase(suffixName) || ".ico".equalsIgnoreCase(suffixName)
                || ".bmp".equalsIgnoreCase(suffixName) ){
            return true;
        }else {
            return false;
        }
    }


}
