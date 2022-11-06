package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.NotifyAnnounceDetails;
import com.wmz.campusplatform.details.UserDetails;
import com.wmz.campusplatform.pojo.NotifyAnnounce;
import com.wmz.campusplatform.pojo.Status;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Log4j2
public class NotifyAnnounceDetailConvert {
    @Autowired
    UserRepository userRepository;

    public NotifyAnnounce notifyAnnounceDetailConvert(NotifyAnnounceDetails notifyAnnounceDetails){
        NotifyAnnounce notifyAnnounce = new NotifyAnnounce();
        notifyAnnounce.setTitle(notifyAnnounceDetails.getTitle());
        notifyAnnounce.setContent(notifyAnnounceDetails.getContent());
        User sender = userRepository.findByStuIdAndRole(notifyAnnounceDetails.getSenderStuId(), notifyAnnounceDetails.getSenderRole());
        if (sender != null){
            notifyAnnounce.setSender(sender);
        }else{
            log.error("用户不存在");
        }
        notifyAnnounce.setCreateTime(new Date());
        notifyAnnounce.setReceiverList(userRepository.findByRoleIn(notifyAnnounceDetails.getReceiverRole()));
        return notifyAnnounce;
    }

}
