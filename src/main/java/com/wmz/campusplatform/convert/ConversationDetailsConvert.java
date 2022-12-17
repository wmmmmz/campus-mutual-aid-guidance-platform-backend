package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.details.MessageDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ConversationRepository;
import com.wmz.campusplatform.repository.MessageRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.List;

@Component
public class ConversationDetailsConvert {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private MessageRepository messageRepository;

    public ConversationDetails conversationDetailConvert(String stuId, Conversation conversation){
        String conversationName = conversation.getName();
        String[] stuIdList = conversationName.split("_");
        String avatarUrlBaseStuId = stuIdList[0].equals(stuId) ? stuIdList[1] : stuIdList[0];
        User avatarUser = userRepository.findByStuIdAndRole(avatarUrlBaseStuId, Role.student.name());
        String imgUrl = avatarUser.getImgUrl();
        List<Img> imgListByImgUrl = mongoDBService.getImgListByImgUrl(imgUrl);
        Img img = imgListByImgUrl.get(0);
        String imgPre = img.getImgPre();
        byte[] imgFile = img.getImgFile();
        String avatar = imgPre + "," + Base64.getEncoder().encodeToString(imgFile);
        String name = avatarUser.getName();
        String content = "";
        List<Message> contentList = messageRepository.findContentListByConversationId(conversation.getId());
        Message message;
        String time = "";
        if (contentList.size() != 0){
            message = contentList.get(0);
            content = message.getContent();
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            time = format.format(message.getPublishTime());
        }
        Integer unreadCnt = conversationRepository.findUnreadCntByConversationUdAndUserId(conversation.getId()
                , userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId());
        return new ConversationDetails(name, avatarUrlBaseStuId, content, avatar, unreadCnt, time);
    }
}
