package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Date;
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

    @Query(nativeQuery = true, value = "SELECT  c.name as className, c2.name as courseName, c.day\n" +
            ", c.start_time as startTime, c.end_time as endTime, room.room_name as roomName\n" +
            "FROM class c \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN room ON c.room_id = room.id \n" +
            "WHERE t.term = :termName AND c.status = :status\n" +
            "AND (c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n")
    List<Map<String, Object>> findByStatusAndTermName(String query, String termName, String status);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO teach_enroll (user_id, class_id, enroll_date, resume)\n" +
            "VALUES (:userId, :classId, :enrollDate, :resumeName)")
    void saveTeachEnroll(Integer userId, Integer classId, Date enrollDate, String resumeName);

    @Query(nativeQuery = true, value = "SELECT * FROM TEACH_ENROLL WHERE user_id = :userId AND class_id = :classId")
    List<Map<String, Object>> findTeachEnroll(Integer userId, Integer classId);

    @Query(nativeQuery = true, value = "SELECT u.name AS studentName, u.tel AS studentTel, u.wx As studentWx" +
            ", u.class_name AS studentClass, c.name AS className, c2.name AS courseName, c.day, c.start_time AS startTime\n" +
            "       , c.end_time AS endTime, room.room_name AS classroom, te.status\n" +
            "       , te.enroll_date AS enrollDate, te.interview_start_date, te.interview_end_date\n" +
            "       , te.success_date AS successDate, te.interview_link AS interviewLink, te.resume \n" +
            "FROM teach_enroll te \n" +
            "LEFT JOIN `user` u ON u.id = te.user_id \n" +
            "LEFT JOIN class c ON c.id = class_id \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term ON term.id = c2.term_id \n" +
            "LEFT JOIN room ON room.id = c.room_id \n" +
            "WHERE term.term = :termName AND u.stu_id = :stuId AND (c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR te.enroll_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR te.interview_start_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR te.interview_end_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR te.success_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%')" +
            "OR te.status LIKE CONCAT('%' ,ifNull(:query,'') ,'%')) ")
    List<Map<String, Object>> getTeachEnrollDataList(String query, String termName, String stuId);

    @Query(nativeQuery = true, value = "SELECT u.name AS studentName, u.tel AS studentTel, u.wx As studentWx" +
            ", u.class_name AS studentClass, c.name AS className, c2.name AS courseName, c.day, c.start_time AS startTime\n" +
            "       , c.end_time AS endTime, room.room_name AS classroom, te.status\n" +
            "       , te.enroll_date AS enrollDate, te.interview_start_date, te.interview_end_date\n" +
            "       , te.success_date AS successDate, te.interview_link AS interviewLink, te.resume \n" +
            "FROM teach_enroll te \n" +
            "LEFT JOIN `user` u ON u.id = te.user_id \n" +
            "LEFT JOIN class c ON c.id = class_id \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term ON term.id = c2.term_id \n" +
            "LEFT JOIN room ON room.id = c.room_id \n" +
            "WHERE term.term = :termName AND (c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR te.enroll_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR te.interview_start_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR te.interview_end_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%') " +
            "OR te.success_date LIKE CONCAT('%' ,ifNull(:query,'') ,'%')" +
            "OR te.status LIKE CONCAT('%' ,ifNull(:query,'') ,'%')) ")
    List<Map<String, Object>> getAllTeachEnrollDataList(String query, String termName);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE teach_enroll ts \n" +
            "LEFT JOIN `user` ON ts.user_id = `user`.id \n" +
            "SET ts.status = '安排面试', ts.interview_link = :interviewLink\n" +
            ", ts.interview_start_date = :startTime, ts.interview_end_date = :endTime\n" +
            "WHERE `user`.name = :studentName AND ts.class_id = :classId")
    void updateStatusToArrangeInterview(Integer classId, String studentName, String interviewLink, Date startTime, Date endTime);

    @Query(nativeQuery = true, value = "SELECT te.status\n" +
            "FROM teach_enroll te\n" +
            "LEFT JOIN `user` ON te.user_id = `user`.id \n" +
            "WHERE `user`.name = :username AND te.class_id = :classId")
    String findStatusByUsernameAndClassId(String username, Integer classId);
}
