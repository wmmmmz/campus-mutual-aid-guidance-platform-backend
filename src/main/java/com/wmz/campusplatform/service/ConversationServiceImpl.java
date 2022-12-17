package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.Conversation;
import com.wmz.campusplatform.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService{
    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public Boolean conversationExist(String stuId, String myStuId) {
        List<String> conversationNameList = new ArrayList<>();
        conversationNameList.add(stuId + "_" + myStuId);
        conversationNameList.add(myStuId + "_" + stuId);
        List<Conversation> conversationList = conversationRepository.findConversationByNameIn(conversationNameList);
        if (conversationList.size() != 0){
            return true;
        }else {
            return false;
        }
    }
}
