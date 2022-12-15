package com.wmz.campusplatform.service;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ClassServiceImpl implements ClassService {
    @Override
    public Boolean TencentMeetingValid(String tencentMeeting) {
        String pattern = "^\\d{9}$";
        return Pattern.matches(pattern, tencentMeeting);
    }
}
