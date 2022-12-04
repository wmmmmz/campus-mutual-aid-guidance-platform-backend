package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.NotifyAnnounce;
import com.wmz.campusplatform.pojo.Role;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.NotifyAnnounceRepository;
import com.wmz.campusplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotifyServiceImpl implements NotifyService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotifyAnnounceRepository notifyAnnounceRepository;

    /**
     * 系统自动发送通知
     * @param theme
     * @param receiveList
     * @param description
     */
    @Override
    public void adminSendNotifyToSpecificUser(String theme, List<User> receiveList, String description) {
        NotifyAnnounce notifyAnnounce = new NotifyAnnounce();
        notifyAnnounce.setTitle(theme);
        notifyAnnounce.setContent(description);
        List<User> userList = userRepository.findByRole(Role.admin.name());
        notifyAnnounce.setSender(userList.get(0));
        //avoid shared reference
        notifyAnnounce.setReceiverList(new ArrayList<>());
        notifyAnnounce.getReceiverList().addAll(receiveList);
        notifyAnnounce.setCreateTime(new Date());
        notifyAnnounce.setAuto(true);
        notifyAnnounceRepository.save(notifyAnnounce);
    }
}
