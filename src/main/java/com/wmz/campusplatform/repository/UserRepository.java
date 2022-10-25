package com.wmz.campusplatform.repository;

import com.wmz.campusplatform.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByStuIdAndPwdAndRole(String stuId, String pwd, String role);

    User findByStuIdAndPwd(String stuId, String pwd);
}
