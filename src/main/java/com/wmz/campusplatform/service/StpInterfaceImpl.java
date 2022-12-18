package com.wmz.campusplatform.service;

import cn.dev33.satoken.stp.StpInterface;
import com.wmz.campusplatform.pojo.Role;
import com.wmz.campusplatform.pojo.User;
import com.wmz.campusplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> list = new ArrayList<String>();
        Optional<User> optionalUser = userRepository.findById(Integer.parseInt((String) loginId));
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            String role = user.getRole();
            if (Role.student.name().equals(role)){
                list.add("student");
                return list;
            }
            if (Role.teacher.name().equals(role)){
                list.add("teacher");
                return list;
            }
            if (Role.admin.name().equals(role)){
                list.add("admin");
                return list;
            }
            if (Role.superAdmin.name().equals(role)){
                list.add("superAdmin");
                return list;
            }
        }
        return list;
    }

}
