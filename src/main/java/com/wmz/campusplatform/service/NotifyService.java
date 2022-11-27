package com.wmz.campusplatform.service;

public interface NotifyService {

    void adminSendNotifyToSpecificUser(String theme, String userName, String description);
}
