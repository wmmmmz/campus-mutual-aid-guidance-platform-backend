package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface TermRepository extends JpaRepository<Term, Integer> {

    Term findByTerm(String name);

    @Query(nativeQuery = true, value = "SELECT term.term ,term.start_time as startTime, term.end_time as endTime, COUNT(DISTINCT course.id) as courseCnt , COUNT(DISTINCT class.id) as classCnt \n" +
            "FROM term \n" +
            "left JOIN course on term.id = course.term_id \n" +
            "left JOIN class on class.course_id = course.id AND class.status = '已开班'\n" +
            "WHERE term.term like CONCAT('%' ,ifNull(:query,'') ,'%') OR term.start_time like CONCAT('%' ,ifNull(:query,'') ,'%') OR term.end_time  like CONCAT('%' ,ifNull(:query,'') ,'%')\n" +
            "GROUP BY term.id ")
    List<Map<String, Object>> findTermList(String query);

    @Transactional
    void deleteByTerm(String term);

    @Query(nativeQuery = true, value = "select * from term where date(:date) between term.start_time AND term.end_time")
    Term findTermByDate(Date date);
}
