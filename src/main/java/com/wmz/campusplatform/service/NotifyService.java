package com.wmz.campusplatform.service;

import com.wmz.campusplatform.pojo.User;

import java.util.List;

public interface NotifyService {

    void adminSendNotifyToSpecificUser(String theme, List<User> receiveList, String description);
}
