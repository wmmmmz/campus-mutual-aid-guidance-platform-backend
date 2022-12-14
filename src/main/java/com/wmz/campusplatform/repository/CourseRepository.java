package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    @Query(nativeQuery = true, value = "SELECT c.name, COUNT(c2.id) as classCnt\n" +
            "FROM course c \n" +
            "LEFT JOIN term t ON t.id  = c.term_id \n" +
            "LEFT JOIN class c2 ON c2.course_id  = c.id AND c2.status = '已开班' \n" +
            "WHERE t.term  = :termName\n" +
            "AND (t.term like CONCAT('%' ,ifNull(:query,'') ,'%') OR c.name  like CONCAT('%' ,ifNull(:query,'') ,'%') )\n" +
            "GROUP BY c.name " +
            "LIMIT :limit OFFSET :offset")
    List<Map<String, Object>> findCourseDetailListByPage(String query, String termName, Integer limit, Integer offset);

    @Query(nativeQuery = true, value = "SELECT c.name, COUNT(c2.id) as classCnt\n" +
            "FROM course c \n" +
            "LEFT JOIN term t ON t.id  = c.term_id \n" +
            "LEFT JOIN class c2 ON c2.course_id  = c.id AND c2.status = '已开班' \n" +
            "WHERE t.term  = :termName\n" +
            "AND (t.term like CONCAT('%' ,ifNull(:query,'') ,'%') OR c.name like CONCAT('%' ,ifNull(:query,'') ,'%') )\n" +
            "GROUP BY c.name")
    List<Map<String, Object>> findCourseDetailList(String query, String termName);

    @Query(nativeQuery = true, value = "SELECT COUNT(*)" +
            "FROM course c \n" +
            "LEFT JOIN term t ON t.id  = c.term_id " +
            "WHERE t.term  = :termName \n" +
            "AND (t.term like CONCAT('%' ,ifNull(:query,'') ,'%') OR c.name like CONCAT('%' ,ifNull(:query,'') ,'%') )\n")
    Integer findCourseTotalSizeByTerm(String query, String termName);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE course \n" +
            "LEFT JOIN term ON term.id = course.term_id\n" +
            "SET course.name = :courseName\n" +
            "WHERE course.name = :originalCourseName AND term.term  = :termName")
    void updateCourseName(String courseName, String originalCourseName, String termName);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE course\n" +
            "FROM course \n" +
            "LEFT JOIN term ON term.id = course.term_id\n" +
            "WHERE course.name = :courseName AND term.term  = :termName")
    void deleteCourse(String courseName, String termName);

    @Query(nativeQuery = true, value = "SELECT c.id, c.name , c.term_id  \n" +
            "FROM course c \n" +
            "LEFT JOIN term t ON t.id = c.term_id \n" +
            "WHERE t.term = :termName AND c.name = :courseName")
    Course findByTermNameAndCourseName(String termName, String courseName);
}
