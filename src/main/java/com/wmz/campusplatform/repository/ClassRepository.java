package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Class;
import com.wmz.campusplatform.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
//    @Query(nativeQuery = true, value = "SELECT  c.name as className, c2.name as courseName, c.day\n" +
//            ", c.start_time as startTime, c.end_time as endTime, room.room_name as roomName\n" +
//            ", c.tencent_meeting as tencentMeeting, `user`.name as teacherName, `user`.tel as teacherTel, " +
//            "`user`.wx as teacherWx, `user`.class_name as teacherClass, COUNT(DISTINCT sec.id) as studentCnt, c.status\n" +
//            "FROM class c \n" +
//            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
//            "LEFT JOIN term t ON t.id = c2.term_id \n" +
//            "LEFT JOIN student_enroll_class sec ON sec.class_id = c.id \n" +
//            "LEFT JOIN room ON c.room_id = room.id \n" +
//            "LEFT JOIN `user` ON `user`.id = c.user_id \n" +
//            "WHERE t.term = :termName AND \n" +
//            "(c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
//            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
//            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
//            "OR c.tencent_meeting LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR `user`.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n" +
//            "GROUP BY c.id ")
//    List<Map<String, Object>> findClassDataList(String query, String termName);


    @Query(value = "SELECT aClass FROM Class AS aClass " +
            "LEFT JOIN aClass.room AS room " +
            "LEFT JOIN aClass.user AS user " +
            "LEFT JOIN aClass.course AS course " +
            "LEFT JOIN aClass.course.term AS term " +
            "WHERE term.term = ?2 AND " +
            "(aClass.name LIKE CONCAT('%' , ?1 ,'%') OR course.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.day LIKE CONCAT('%' , ?1 ,'%') " +
            "OR room.roomName LIKE CONCAT('%' , ?1 ,'%') " +
            "OR user.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.status LIKE CONCAT('%' , ?1 ,'%'))")
    List<Class> findClassDataListByPage(String query, String termName, Pageable pageable);

    @Query(value = "SELECT COUNT(aClass) FROM Class AS aClass " +
            "LEFT JOIN aClass.room AS room " +
            "LEFT JOIN aClass.user AS user " +
            "LEFT JOIN aClass.course AS course " +
            "LEFT JOIN aClass.course.term AS term " +
            "WHERE term.term = ?2 AND " +
            "(aClass.name LIKE CONCAT('%' , ?1 ,'%') OR course.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.day LIKE CONCAT('%' , ?1 ,'%') " +
            "OR room.roomName LIKE CONCAT('%' , ?1 ,'%') " +
            "OR user.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.status LIKE CONCAT('%' , ?1 ,'%'))")
    Integer findClassTotalSizeByTerm(String query, String termName);

    @Query(value = "SELECT aClass FROM Class AS aClass " +
            "LEFT JOIN aClass.room AS room " +
            "LEFT JOIN aClass.user AS user " +
            "LEFT JOIN aClass.course AS course " +
            "LEFT JOIN aClass.course.term AS term " +
            "WHERE term.term = ?2 AND user.id = ?4 AND aClass.status = ?3 AND " +
            "(aClass.name LIKE CONCAT('%' , ?1 ,'%') OR course.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.day LIKE CONCAT('%' , ?1 ,'%') " +
            "OR room.roomName LIKE CONCAT('%' , ?1 ,'%') " +
            "OR user.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.status LIKE CONCAT('%' , ?1 ,'%'))"   )
    List<Class> findMyTeachClassDataListByPage(String query, String termName, String status, Integer userId, Pageable pageable);


    @Query(value = "SELECT COUNT(aClass) FROM Class AS aClass " +
            "LEFT JOIN aClass.room AS room " +
            "LEFT JOIN aClass.user AS user " +
            "LEFT JOIN aClass.course AS course " +
            "LEFT JOIN aClass.course.term AS term " +
            "WHERE term.term = ?2 AND user.id = ?4 AND aClass.status = ?3 AND " +
            "(aClass.name LIKE CONCAT('%' , ?1 ,'%') OR course.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.day LIKE CONCAT('%' , ?1 ,'%') " +
            "OR room.roomName LIKE CONCAT('%' , ?1 ,'%') " +
            "OR user.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.status LIKE CONCAT('%' , ?1 ,'%'))"   )
    Integer findMyTeachClassTotalSize(String query, String termName, String status, Integer userId);

    @Query(value = "SELECT aClass FROM Class AS aClass " +
            "LEFT JOIN aClass.room AS room " +
            "LEFT JOIN aClass.user AS user " +
            "LEFT JOIN aClass.course AS course " +
            "LEFT JOIN aClass.course.term AS term " +
            "WHERE term.term = ?2 AND aClass.status = ?3 AND ?4 IN (aClass.studentList) AND " +
            "(aClass.name LIKE CONCAT('%' , ?1 ,'%') OR course.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.day LIKE CONCAT('%' , ?1 ,'%') " +
            "OR room.roomName LIKE CONCAT('%' , ?1 ,'%') " +
            "OR user.name LIKE CONCAT('%' , ?1 ,'%') OR aClass.status LIKE CONCAT('%' , ?1 ,'%'))"   )
    List<Class> findClassILearnDataList(String query, String termName, String status, User student);

    @Query(nativeQuery = true, value = "SELECT * \n" +
            "FROM class c \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON c2.term_id = t.id \n" +
            "WHERE t.term = :termName AND c.name = :className")
    List<Class> findByTermNameAndClassName(String termName, String className);

    @Query(nativeQuery = true, value = "SELECT c.name as className, c2.name as courseName, c.day\n" +
            ", c.start_time as startTime, c.end_time as endTime, room.room_name as roomName, u.name AS teacherName" +
            ", u.tel AS teacherTel, u.wx AS teacherWx, u.class_name AS teacherClass \n" +
            "FROM class c \n" +
            "LEFT JOIN `user` u ON u.id = c.user_id \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN room ON c.room_id = room.id \n" +
            "WHERE t.term = :termName AND c.status = :status AND c.max_student_count > (" +
            "SELECT COUNT(*) AS currentStudentCount\n" +
            "FROM student_enroll_class sec\n" +
            "WHERE sec.class_id = c.id" +
            ")\n" +
            "AND (c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')" +
            "OR u.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n " +
            "LIMIT :limit OFFSET :offset")
    List<Map<String, Object>> findByStatusAndTermNameByPage(String query, String termName, String status, Integer limit, Integer offset);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) \n" +
            "FROM class c \n" +
            "LEFT JOIN `user` u ON u.id = c.user_id \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN room ON c.room_id = room.id \n" +
            "WHERE t.term = :termName AND c.status = :status AND c.max_student_count > (" +
            "SELECT COUNT(*) AS currentStudentCount\n" +
            "FROM student_enroll_class sec\n" +
            "WHERE sec.class_id = c.id" +
            ")\n" +
            "AND (c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')" +
            "OR u.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n ")
    Integer getClassTotalSizeByStatusAndTerm(String query, String termName, String status);

    @Query("SELECT aClass " +
            "FROM Class AS aClass " +
            "LEFT JOIN  aClass.course " +
            "LEFT JOIN  aClass.course.term AS Term " +
            "WHERE aClass.status = ?1 AND Term.term = ?2")
    List<Class> findByStatusAndTerm(String status, String termName);

    List<Class> findByStatus(String status);

    @Query(nativeQuery = true, value = "SELECT u.name ,COUNT(*) AS teachCnt \n" +
            "FROM `class` c \n" +
            "LEFT JOIN `user` u ON u.id = c.user_id \n" +
            "WHERE c.status = '?????????' AND c.user_id != ''\n" +
            "GROUP BY c.user_id \n" +
            "ORDER BY teachCnt DESC")
    List<Map<String, Object>> getStarTeacher();
    //----------------------TeachEnroll-------------------------
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
            "       , te.success_date AS successDate, te.interview_link AS interviewLink, te.resume, te.pass_date AS passDate \n" +
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
            "OR te.status LIKE CONCAT('%' ,ifNull(:query,'') ,'%')) " +
            "LIMIT :limit OFFSET :offset")
    List<Map<String, Object>> getTeachEnrollDataListByPage(String query, String termName, String stuId, Integer limit, Integer offset);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) " +
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
            "OR te.status LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))")
    Integer getTeachEnrollTotalSize(String query, String termName, String stuId);

    @Query(nativeQuery = true, value = "SELECT u.name AS studentName, u.tel AS studentTel, u.wx As studentWx" +
            ", u.class_name AS studentClass, c.name AS className, c2.name AS courseName, c.day, c.start_time AS startTime\n" +
            "       , c.end_time AS endTime, room.room_name AS classroom, te.status\n" +
            "       , te.enroll_date AS enrollDate, te.interview_start_date, te.interview_end_date\n" +
            "       , te.success_date AS successDate, te.interview_link AS interviewLink, te.resume, te.remark \n" +
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
            "OR te.status LIKE CONCAT('%' ,ifNull(:query,'') ,'%')) " +
            "LIMIT :limit OFFSET :offset")
    List<Map<String, Object>> getAllTeachEnrollDataListByPage(String query, String termName, Integer limit, Integer offset);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) " +
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
    Integer getAllTeachEnrollTotalSizeByTerm(String query, String termName);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE teach_enroll ts \n" +
            "LEFT JOIN `user` ON ts.user_id = `user`.id \n" +
            "SET ts.status = '????????????', ts.interview_link = :interviewLink\n" +
            ", ts.interview_start_date = :startTime, ts.interview_end_date = :endTime\n" +
            "WHERE `user`.name = :studentName AND ts.class_id = :classId")
    void updateStatusToArrangeInterview(Integer classId, String studentName, String interviewLink, Date startTime, Date endTime);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE teach_enroll ts \n" +
            "LEFT JOIN `user` ON ts.user_id = `user`.id \n" +
            "SET ts.status = '????????????', ts.success_date = :successDate\n" +
            "WHERE `user`.name = :studentName AND ts.class_id = :classId")
    void updateStatusToHired(Integer classId, String studentName, Date successDate);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE teach_enroll ts \n" +
            "LEFT JOIN `user` ON ts.user_id = `user`.id \n" +
            "SET ts.status = '????????????', ts.pass_date = :passDate\n" +
            "WHERE `user`.name = :studentName AND ts.class_id = :classId")
    void updateStatusToPassed(Integer classId, String studentName, Date passDate);

    @Query(nativeQuery = true, value = "SELECT te.status\n" +
            "FROM teach_enroll te\n" +
            "LEFT JOIN `user` ON te.user_id = `user`.id \n" +
            "WHERE `user`.name = :username AND te.class_id = :classId")
    String findStatusByUsernameAndClassId(String username, Integer classId);

    @Query(nativeQuery = true, value = "SELECT te.status \n" +
            "FROM teach_enroll te\n" +
            "WHERE te.status = :status AND te.class_id = :classId")
     String findTeachEnrollByClassIdAndStatus(Integer classId, String status);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE teach_enroll ts \n" +
            "LEFT JOIN `user` ON ts.user_id = `user`.id \n" +
            "SET ts.status = '????????????', ts.interrupt_date = :interruptDate, ts.remark = :reason\n" +
            "WHERE `user`.name = :studentName AND ts.class_id = :classId")
    void updateStatusToInterrupted(String reason, Integer classId, String studentName, Date interruptDate);

    //-----------ClassEnroll---------------

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO student_enroll_class (user_id, class_id, enroll_date)\n" +
            "VALUES (:userId, :classId, :enrollDate)")
    void saveClassEnroll(Integer userId, Integer classId, Date enrollDate);

    @Query(nativeQuery = true, value = "SELECT * FROM student_enroll_class WHERE user_id = :userId AND class_id = :classId")
    List<Map<String, Object>> findClassEnroll(Integer userId, Integer classId);

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
            "WHERE t.term = :termName AND c.id IN (" +
            "SELECT c2.id " +
            "FROM class c2 " +
            "LEFT JOIN student_enroll_class sec2 ON sec2.class_id = c2.id \n" +
            "WHERE sec2.user_id = :userId" +
            ") AND\n" +
            "(c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.tencent_meeting LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR `user`.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n" +
            "GROUP BY c.id " +
            "LIMIT :limit OFFSET :offset")
    List<Map<String, Object>> getMyClassEnrollDataListByPage(String query, String termName, Integer userId, Integer limit, Integer offset);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) " +
            "FROM class c \n" +
            "LEFT JOIN course c2 ON c2.id = c.course_id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN student_enroll_class sec ON sec.class_id = c.id \n" +
            "LEFT JOIN room ON c.room_id = room.id \n" +
            "LEFT JOIN `user` ON `user`.id = c.user_id \n" +
            "WHERE t.term = :termName AND sec.user_id = :userId AND\n" +
            "(c.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c2.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%') \n" +
            "OR c.day LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR c.start_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.end_time LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR room.room_name LIKE CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "OR c.tencent_meeting LIKE CONCAT('%' ,ifNull(:query,'') ,'%') OR `user`.name LIKE CONCAT('%' ,ifNull(:query,'') ,'%'))\n")
    Integer getMyClassEnrollTotalSizeByTerm(String query, String termName, Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE " +
            "FROM student_enroll_class \n" +
            "WHERE user_id = :userId AND class_id = :classId")
    void deleteClassEnroll(Integer classId, Integer userId);

    @Query(nativeQuery = true, value = "SELECT c.name, c.start_time, c.end_time , c.`day`, r.room_name, c.tencent_meeting \n" +
            "FROM student_enroll_class sec \n" +
            "LEFT JOIN `user` u ON u.id = sec.user_id \n" +
            "LEFT JOIN class c ON c.id = sec.class_id \n" +
            "LEFT JOIN course c2 ON c.course_id = c2.id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN room r ON r.id = c.room_id \n" +
            "WHERE c.status = '?????????' AND u.id = :userId AND t.term = :termName\n" +
            "ORDER BY c.start_time ASC, c.`day` ASC")
    List<Map<String, Object>> getClassTable(Integer userId, String termName);

    @Query(nativeQuery = true, value = "SELECT c.name, c.start_time, c.end_time , c.`day`, r.room_name, c.tencent_meeting \n" +
            "FROM class c \n" +
            "LEFT JOIN `user` u ON u.id = c.user_id \n" +
            "LEFT JOIN course c2 ON c.course_id = c2.id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "LEFT JOIN room r ON r.id = c.room_id \n" +
            "WHERE c.status = '?????????' AND u.id = :userId AND t.term = :termName\n" +
            "ORDER BY c.start_time ASC, c.`day` ASC")
    List<Map<String, Object>> getTeachTable(Integer userId, String termName);
}
