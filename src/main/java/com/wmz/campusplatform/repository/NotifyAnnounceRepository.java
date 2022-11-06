package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.NotifyAnnounce;
import com.wmz.campusplatform.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface NotifyAnnounceRepository extends JpaRepository<NotifyAnnounce, Integer> {

    @Query(nativeQuery = true, value = "select title, content, create_time as createTime, count(*) as unreadCnt\n" +
            "from notify_announce \n" +
            "left join notify_announce_receiver nar on nar.notify_announce_id = notify_announce.id  \n" +
            "where sender_id = :senderId AND nar.state = 'UNREADED' AND " +
            "(title like CONCAT('%' ,ifNull(:query,'') ,'%') OR content like CONCAT('%' ,ifNull(:query,'') ,'%') OR create_time like CONCAT('%' ,ifNull(:query,'') ,'%')\n)" +
            "group by notify_announce.id")
    List<Map<String, Object>> findBySenderAndQuery(Integer senderId, String query);

    @Transactional
    void deleteByTitleAndContentAndCreateTime(String title, String content, Date creatTime);

}
