package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Conversation;
import com.wmz.campusplatform.pojo.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(nativeQuery = true, value = "SELECT * " +
            "FROM message m " +
            "WHERE m.conversation_id = :conversationId " +
            "ORDER BY m.publish_time DESC ")
    List<Message> findContentListByConversationId(Integer conversationId);

    @Query(nativeQuery = true, value = "SELECT * FROM message m " +
            "LEFT JOIN conversation c ON c.id = m.conversation_id " +
            "WHERE c.name IN :conversationNameList " +
            "ORDER BY m.publish_time DESC")
    List<Message> findMessageByConversationNameList(List<String> conversationNameList);
}
