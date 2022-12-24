package com.wmz.campusplatform.service;

import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.details.MessageDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
    List<ConversationDetails> getMyConversation(String stuId);

    void saveMessage(String stuId, String myStuId, String content, Boolean isFile, List<String> tempFilePath
            , List<String> suffixName, List<String> fileName);

    List<MessageDetails> getMessageList(String myStuId, String stuId, Integer startIndex, Integer pageSize);

    /**
     * change the time format for client
     * @param time
     * @return
     */
    String getTimeByDate(String time);
}
