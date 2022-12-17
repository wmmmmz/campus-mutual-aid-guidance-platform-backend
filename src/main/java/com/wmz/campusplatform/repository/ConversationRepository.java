package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Conversation;
import com.wmz.campusplatform.pojo.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {

    List<Conversation> findConversationByNameIn(List<String> nameList);

    @Query(nativeQuery = true, value = "SELECT uc.unread_cnt " +
            "FROM user_conversation uc " +
            "WHERE uc.user_id = :userId AND uc.conversation_id = :conversationId")
    Integer findUnreadCntByConversationUdAndUserId(Integer conversationId, Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_conversation uc " +
            "SET uc.unread_cnt = uc.unread_cnt + 1 " +
            "WHERE uc.user_id = :userId AND uc.conversation_id = :conversationId")
    void updateUnreadCntByConversationIdAndUserId(Integer conversationId, Integer userId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE user_conversation uc " +
            "SET uc.unread_cnt = 0 " +
            "WHERE uc.user_id = :userId AND uc.conversation_id = :conversationId")
    void clearUnreadCntByConversationIdAndUserId(Integer conversationId, Integer userId);
}
