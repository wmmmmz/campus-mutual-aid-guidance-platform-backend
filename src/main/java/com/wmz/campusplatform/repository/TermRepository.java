package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TermRepository extends JpaRepository<Term, Integer> {

    Term findByTerm(String name);

    @Query(nativeQuery = true, value = "SELECT term.term ,term.start_time as startTime, term.end_time as endTime, COUNT(DISTINCT course.id) as courseCnt , COUNT(DISTINCT class.id) as classCnt \n" +
            "FROM term \n" +
            "left JOIN course on term.id = course.term_id \n" +
            "left JOIN class on class.course_id = course.id AND class.status = 'SUCCESS'\n" +
            "GROUP BY term.id ")
    List<Map<String, Object>> findTermList();

}
