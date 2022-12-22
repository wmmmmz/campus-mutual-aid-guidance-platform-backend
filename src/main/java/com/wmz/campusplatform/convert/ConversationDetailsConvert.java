package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.ConversationDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.repository.ConversationRepository;
import com.wmz.campusplatform.repository.MessageRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.service.MongoDBService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Log4j2
public class ConversationDetailsConvert {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private MessageRepository messageRepository;

    private List<String> EmojiList = new ArrayList<>(Arrays.asList("占位", "微笑", "撇嘴", "色", "发呆", "得意", "流泪", "害羞", "闭嘴", "睡", "大哭",
            "尴尬", "发怒", "调皮", "呲牙", "惊讶", "难过", "酷", "冷汗", "抓狂", "吐", "偷笑", "可爱",
            "白眼", "傲慢", "饥饿", "困", "惊恐", "流汗", "憨笑", "大兵", "奋斗", "咒骂", "疑问", "嘘",
            "晕", "折磨", "衰", "骷髅", "敲打", "再见", "擦汗", "抠鼻", "鼓掌", "糗大了", "坏笑", "左哼哼",
            "右哼哼", "哈欠", "鄙视", "委屈", "快哭了", "阴险", "亲亲", "吓", "可怜", "菜刀", "西瓜", "啤酒",
            "篮球", "乒乓", "咖啡", "饭", "猪头", "玫瑰", "凋谢", "示爱", "爱心", "心碎", "蛋糕", "闪电", "炸弹",
            "刀", "足球", "瓢虫", "便便", "月亮", "太阳", "礼物", "拥抱", "强", "弱", "握手", "胜利", "抱拳", "勾引",
            "拳头", "差劲", "爱你", "NO", "OK", "爱情", "飞吻", "跳跳", "发抖", "怄火", "转圈", "磕头", "回头", "跳绳", "挥手",
            "激动", "街舞", "献吻", "左太极", "右太极"));

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
            if (message.getImg()){
                content = "[图片]";
            } else if (message.getFile()) {
                content = "[文件]";
            } else {
                Pattern pattern = Pattern.compile("<img src=\"https:/rescdn.qqmail.com/node/wwopen/wwopenmng/images/qq_emotion/qq/[^.]*.png\"  style= \"width: 22px;height: 22px\">");
                StringBuilder operatorStr = new StringBuilder(message.getContent());
                Matcher m = pattern.matcher(operatorStr);
                while (m.find()) {
                    //使用分组进行替换
                    String[] split = m.group().split("qq/")[1].split("\\.");
                    String replacement = "[" + EmojiList.get(Integer.parseInt(split[0])) + "]";
                    operatorStr.replace(m.start(), m.end(), replacement);
                    m = pattern.matcher(operatorStr);
                }
                content = operatorStr.toString();
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            time = format.format(message.getPublishTime());
        }
        Integer unreadCnt = conversationRepository.findUnreadCntByConversationUdAndUserId(conversation.getId()
                , userRepository.findByStuIdAndRole(stuId, Role.student.name()).getId());
        return new ConversationDetails(name, avatarUrlBaseStuId, content, avatar, unreadCnt, time);
    }
}
