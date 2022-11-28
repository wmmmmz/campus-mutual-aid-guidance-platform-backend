package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.NotifyAnnounce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
            "where is_auto = :isAuto AND sender_id = :senderId AND nar.status = 'UNREADED' AND " +
            "(title like CONCAT('%' ,ifNull(:query,'') ,'%') OR content like CONCAT('%' ,ifNull(:query,'') ,'%') OR create_time like CONCAT('%' ,ifNull(:query,'') ,'%')\n)" +
            "group by notify_announce.id")
    List<Map<String, Object>> findBySenderAndQuery(Integer senderId, String query, Boolean isAuto);

    @Transactional
    void deleteByTitleAndContentAndCreateTime(String title, String content, Date creatTime);

    List<NotifyAnnounce> findNotifyAnnounceByTitleAndContentAndCreateTime(String title, String content, Date creatTime);

    @Query(nativeQuery = true, value = "select na.title, na.content, na.create_time as createTime, nar.status\n" +
            "from notify_announce_receiver nar \n" +
            "left join `user` on `user`.id = nar.receiver_id \n" +
            "left join notify_announce na ON notify_announce_id = na.id \n" +
            "where `user`.id = :id  ")
    List<Map<String, Object>> findNotifyOfUserWithStatus(Integer id);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE notify_announce_receiver nar \n" +
            "SET nar.status = :status\n" +
            "WHERE  nar.notify_announce_id = :notifyId AND nar.receiver_id = :userId")
    void changeStatus(Integer notifyId, Integer userId, String status);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE notify_announce_receiver nar \n" +
            "SET nar.status = :status\n" +
            "WHERE nar.receiver_id = :userId")
    void changeAllStatus(Integer userId, String status);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE \n" +
            "FROM notify_announce_receiver nar\n" +
            "WHERE nar.receiver_id = :userId AND status = 'RECYCLE'")
    void deleteRecycleNotify(Integer userId);

}
