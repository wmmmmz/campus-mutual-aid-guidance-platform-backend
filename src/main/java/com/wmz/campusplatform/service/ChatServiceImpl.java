package com.wmz.campusplatform.service;

import com.wmz.campusplatform.convert.ConversationDetailsConvert;
import com.wmz.campusplatform.convert.MessageDetailsConvert;
import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.details.MessageDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ConversationRepository;
import com.wmz.campusplatform.repository.MessageRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
            SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm");
            SimpleDateFormat formatYear = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatMonth = new SimpleDateFormat("MM-dd");
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = fmt.parse(latestMessageTime);
                if (isToday(date)){
                    conversationDetails.setLatestMessageTime(formatToday.format(date));
                }else if(isThisYear(date)){
                    conversationDetails.setLatestMessageTime(formatMonth.format(date));
                }else {
                    conversationDetails.setLatestMessageTime(formatYear.format(date));
                }
            } catch (ParseException e) {
                log.error("time parse error");
                throw new RuntimeException(e);
            }
        }
        return conversationDetailsList;
    }

    @Override
    public void saveMessage(String stuId, String myStuId, String content) {
        Conversation conversation = getConversationByStuIdList(myStuId, stuId);
        User user = userRepository.findByStuIdAndRole(myStuId, Role.student.name());
        Message message = new Message();
        message.setContent(content);
        message.setUser(user);
        message.setConversation(conversation);
        message.setPublishTime(new Date());
        messageRepository.save(message);
        //add unreadCnt
        if (!stuId.equals(myStuId)){
            conversationRepository.updateUnreadCntByConversationIdAndUserId(conversation.getId()
                    , userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId());
        }
    }

    @Override
    public List<MessageDetails> getMessageList(String myStuId, String stuId) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Message> messageList = messageRepository.findMessageByConversationNameList(conversationNameList);
        List<MessageDetails> messageDetailsList = new ArrayList<>();
        for (Message message : messageList) {
            MessageDetails messageDetails = messageDetailsConvert.messageDetailConvert(message, myStuId);
            messageDetailsList.add(messageDetails);
        }
        return messageDetailsList;
    }

    private Conversation getConversationByStuIdList(String myStuId, String stuId) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Conversation> conversationList = conversationRepository.findConversationByNameIn(conversationNameList);
        return conversationList.get(0);
    }

    public static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }

    public static boolean isToday(Date date) {
        return isThisTime(date, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本年
    public static boolean isThisYear(Date time) {
        return isThisTime(time, "yyyy");
    }
}
