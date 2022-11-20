package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.Room;
import io.swagger.models.auth.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {

    Room findByRoomName(String roomName);

    @Query(nativeQuery = true, value = "SELECT r2.id, r2.room_name, r2.form \n" +
            "FROM room r2\n" +
            "WHERE room_name NOT IN \n" +
            "(SELECT r.room_name \n" +
            "FROM room r \n" +
            "LEFT JOIN class c ON c.room_id = r.id \n" +
            "LEFT JOIN course c2 ON c.course_id = c2.id \n" +
            "LEFT JOIN term t ON t.id = c2.term_id \n" +
            "WHERE t.term = :termName AND c.day = :day\n" +
            "AND (" +
            "((unix_timestamp(c.start_time) >= unix_timestamp(:startTime)) AND (unix_timestamp(c.start_time) <= unix_timestamp(:endTime)) " +
            "OR (unix_timestamp(c.end_time) >= unix_timestamp(:startTime)) AND (unix_timestamp(c.end_time) <= unix_timestamp(:endTime)))" +
            "OR ( (unix_timestamp(c.start_time) >= unix_timestamp(:startTime)) AND (unix_timestamp(c.end_time) <= unix_timestamp(:endTime))  )" +
            "OR (  (unix_timestamp(c.start_time) <= unix_timestamp(:startTime)) AND (unix_timestamp(c.end_time) >= unix_timestamp(:endTime)) )" +
            ")\n" +
            ")")
    List<Room> findFreeRoomByTermNameAndTime(String termName, String startTime, String endTime, String day);
}
