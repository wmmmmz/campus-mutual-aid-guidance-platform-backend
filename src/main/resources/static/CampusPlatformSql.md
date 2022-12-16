```sql
CREATE DATABASE `CampusPlatform`

-- CampusPlatform.class definition

CREATE TABLE `class` (
 `id` int NOT NULL AUTO_INCREMENT,
 `name` varchar(50) NOT NULL,
 `course_id` int NOT NULL,
 `user_id` int DEFAULT NULL,
 `day` varchar(50) NOT NULL,
 `start_time` datetime DEFAULT NULL,
 `end_time` datetime DEFAULT NULL,
 `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'IN_PROGRESS',
 `room_id` int DEFAULT NULL,
 `tencent_meeting` varchar(100) DEFAULT NULL,
 `max_student_count` int DEFAULT NULL,
 PRIMARY KEY (`id`),
 KEY `course_id` (`course_id`),
 KEY `user_id` (`user_id`),
 KEY `class_ibfk_3` (`room_id`),
 CONSTRAINT `class_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
 CONSTRAINT `class_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
 CONSTRAINT `class_ibfk_3` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- CampusPlatform.course definition

CREATE TABLE `course` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `term_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `term_id` (`term_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`term_id`) REFERENCES `term` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- CampusPlatform.`role` definition

CREATE TABLE `role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- CampusPlatform.term definition

CREATE TABLE `term` (
  `id` int NOT NULL AUTO_INCREMENT,
  `term` varchar(100) NOT NULL,
  `start_time` date DEFAULT NULL,
  `end_time` date DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- CampusPlatform.`user` definition

CREATE TABLE `user` (
    `id` int NOT NULL AUTO_INCREMENT,
    `stu_id` varchar(100) NOT NULL,
    `pwd` varchar(100) NOT NULL,
    `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `tel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
    `wx` varchar(100) DEFAULT NULL,
    `role` varchar(100) NOT NULL,
    `description` varchar(100) DEFAULT NULL,
    `interview_status` varchar(100) DEFAULT 'NO_NEED',
    `img_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'default',
    `is_locked` tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- CampusPlatform.notify_announce definition

CREATE TABLE `notify_announce` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `title` varchar(100) NOT NULL,
  `content` text NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_auto` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sender_id` (`sender_id`),
  CONSTRAINT `notify_announce_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





-- CampusPlatform.notify_announce_receiver definition

CREATE TABLE `notify_announce_receiver` (
  `id` int NOT NULL AUTO_INCREMENT,
  `notify_announce_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'UNREADED',
  PRIMARY KEY (`id`),
  KEY `notify_announce_id` (`notify_announce_id`),
  KEY `receiver_id` (`receiver_id`),
  CONSTRAINT `notify_announce_receiver_ibfk_1` FOREIGN KEY (`notify_announce_id`) REFERENCES `notify_announce` (`id`) ON DELETE CASCADE,
  CONSTRAINT `notify_announce_receiver_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- CampusPlatform.student_enroll_class definition
-- 学生报名上课

CREATE TABLE `student_enroll_class` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `class_id` int DEFAULT NULL,
  `enroll_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `class_id` (`class_id`),
  CONSTRAINT `student_enroll_class_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `student_enroll_class_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- CampusPlatform.teach_enroll definition
-- 学生报名授课
CREATE TABLE `teach_enroll` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `class_id` int NOT NULL,
  `status` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '报名成功',
  `enroll_date` date DEFAULT NULL,
  `interview_start_date` datetime DEFAULT NULL,
  `success_date` date DEFAULT NULL,
  `interview_link` varchar(100) DEFAULT NULL,
  `interview_end_date` datetime DEFAULT NULL,
  `resume` varchar(100) DEFAULT NULL,
  `pass_date` date DEFAULT NULL,
  `remark` varchar(100) DEFAULT NULL,
  `interrupt_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `class_id` (`class_id`),
  CONSTRAINT `teach_enroll_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `teach_enroll_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



-- CampusPlatform.room definition

CREATE TABLE `room` (
  `id` int NOT NULL AUTO_INCREMENT,
  `room_name` varchar(50) NOT NULL,
  `form` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





-- CampusPlatform.conversation definition
-- conversation basic information
CREATE TABLE `conversation` (
    `id` int NOT NULL AUTO_INCREMENT,
    `avatar_url` varchar(100) DEFAULT NULL,
    `name` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- CampusPlatform.user_conversation definition
-- userList in conversation
CREATE TABLE `user_conversation` (
     `id` int NOT NULL AUTO_INCREMENT,
     `user_id` int DEFAULT NULL,
     `conversation_id` int DEFAULT NULL,
     PRIMARY KEY (`id`),
     KEY `user_id` (`user_id`),
     KEY `conversation_id` (`conversation_id`),
     CONSTRAINT `user_conversation_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`id`) ON DELETE CASCADE,
     CONSTRAINT `user_conversation_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




-- CampusPlatform.message definition
-- record message 
CREATE TABLE `message` (
   `id` int NOT NULL AUTO_INCREMENT,
   `user_id` int DEFAULT NULL,
   `conversation_id` int DEFAULT NULL,
   `content` varchar(1000) DEFAULT NULL,
   `publish_time` datetime DEFAULT NULL,
   PRIMARY KEY (`id`),
   KEY `user_id` (`user_id`),
   KEY `conversation_id` (`conversation_id`),
   CONSTRAINT `message_ibfk_1` FOREIGN KEY (`conversation_id`) REFERENCES `conversation` (`id`) ON DELETE CASCADE,
   CONSTRAINT `message_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

```

