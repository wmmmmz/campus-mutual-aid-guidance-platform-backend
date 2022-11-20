package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ClassRepository extends JpaRepository<Class, Integer> {
    @Query(nativeQuery = true, value = "SELECT  c.name as className, c2.name as courseName, c.day\n" +
            ", c.start_time as startTime, c.end_time as endTime, room.room_name as roomName\n" +
            ", c.tencent_meeting as tencentMeeting, `user`.name as teacherName, `user`.tel as teacherTel, " +
            "`user`.wx as teacherWx, `user`.class_name as teacherClass, COUNT(DISTINCT sec.id) as studentCnt, c.status\n" +
            "FROM class c \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN student_enroll_class sec ON sec.class_id = c.id \n" +
            "LEFT JOIN room ON c.room_id = room.id \n" +
            "LEFT JOIN `user` ON `user`.id = c.user_id \n" +
            "WHERE t.term = :termName AND \n" +
            "(c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.tencent_meeting LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR `user`.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n" +
            "GROUP BY c.id ")
    List<Map<String, Object>> findClassDataList(String query, String termName);

    @Query(nativeQuery = true, value = "SELECT * \n" +
            "FROM class c \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON c2.term_id = t.id \n" +
            "WHERE t.term = :termName AND c.name = :className")
    List<Class> findByTermNameAndClassName(String termName, String className);
}
