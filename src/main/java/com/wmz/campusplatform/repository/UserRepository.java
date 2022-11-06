package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByStuIdAndPwdAndRole(String stuId, String pwd, String role);

    User findByStuIdAndRole(String stuId, String role);

    User findByStuIdAndPwd(String stuId, String pwd);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE `user`\n" +
            "SET `user`.img_url = :imgUrl\n" +
            "WHERE `user`.stu_id = :stuId AND `user`.`role` = :role")
    void updateImgUrl(String imgUrl, String stuId, String role);

    List<User> findByRoleIn(List<String> roleList);
}
