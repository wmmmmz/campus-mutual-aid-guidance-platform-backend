package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findByRole(String role);

    User findByStuIdAndPwdAndRole(String stuId, String pwd, String role);

    User findByStuIdAndRole(String stuId, String role);

    List<User> findByStuIdAndPwd(String stuId, String pwd);

    User findByNameAndRole(String name, String role);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE `user`\n" +
            "SET `user`.img_url = :imgUrl\n" +
            "WHERE `user`.stu_id = :stuId AND `user`.`role` = :role")
    void updateImgUrl(String imgUrl, String stuId, String role);

    List<User> findByRoleIn(List<String> roleList);

    List<User> findByName(String username);

    @Query(value = "SELECT u " +
            "FROM User AS u WHERE u.role = ?1")
    List<User> findByRoleAndPage(String role, Pageable pageable);
}
