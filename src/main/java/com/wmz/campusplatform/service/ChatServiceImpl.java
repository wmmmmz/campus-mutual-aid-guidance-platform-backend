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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
        return conversationDetailsList.stream()
                .sorted(Comparator.comparing(ConversationDetails::getLatestMessageTime).reversed())
                .collect(Collectors.toList());
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

}
