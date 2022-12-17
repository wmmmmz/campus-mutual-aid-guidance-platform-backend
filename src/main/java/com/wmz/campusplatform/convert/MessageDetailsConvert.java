package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.MessageDetails;
import com.wmz.campusplatform.pojo.Img;
import com.wmz.campusplatform.pojo.Message;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.service.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class MessageDetailsConvert {
    @Autowired
    private MongoDBService mongoDBService;

    public MessageDetails messageDetailConvert(Message message, String myStuId){
        User user = message.getUser();
        String name = "";
        String avatar = "";
        if (user == null){
            return new MessageDetails();
        }
        name = user.getName();
        List<Img> imgList = mongoDBService.getImgListByImgUrl(user.getImgUrl());
        avatar = mongoDBService.getBase64ByImg(imgList.get(0));
        String content = message.getContent();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = format.format(message.getPublishTime());
        return new MessageDetails(name, avatar, user.getStuId().equals(myStuId), content, time);
    }
}
