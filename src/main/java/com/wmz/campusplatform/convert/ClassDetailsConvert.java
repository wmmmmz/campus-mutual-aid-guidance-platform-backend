package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.ClassDetails;
import com.wmz.campusplatform.pojo.*;
import com.wmz.campusplatform.pojo.Class;
import com.wmz.campusplatform.repository.CourseRepository;
import com.wmz.campusplatform.repository.RoomRepository;
import com.wmz.campusplatform.repository.TermRepository;
import com.wmz.campusplatform.repository.UserRepository;
import com.wmz.campusplatform.utils.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Log4j2
public class ClassDetailsConvert {

    @Autowired
    private TermRepository termRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Class classDetailConvert(Class aClass, ClassDetails classDetails){

        aClass.setName(classDetails.getClassName());

        if (StringUtils.isEmpty(classDetails.getTermName())) {
            Term termByDate = termRepository.findTermByDate(new Date());
            if (termByDate == null) {
                log.error("学期不存在");
            } else {
                classDetails.setTermName(termByDate.getTerm());
            }
        }
        Course course = courseRepository.findByTermNameAndCourseName(classDetails.getTermName(), classDetails.getCourseName());
        aClass.setCourse(course);

        aClass.setDay(classDetails.getDay());

        User teacher = userRepository.findByNameAndRole(classDetails.getTeacherName(), Role.teacher.name());
        aClass.setUser(teacher);

        Room room = roomRepository.findByRoomName(classDetails.getClassroom());
        aClass.setRoom(room);

        aClass.setTencentMeeting(classDetails.getTencentMeeting());

        aClass.setStartTime(classDetails.getDateList().get(0));
        aClass.setEndTime(classDetails.getDateList().get(1));
        return aClass;
    }
}
