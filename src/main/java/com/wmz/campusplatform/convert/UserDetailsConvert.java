package com.wmz.campusplatform.convert;

import com.wmz.campusplatform.details.UserDetails;
import com.wmz.campusplatform.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsConvert {

    public UserDetails userConvert(User user, String saTokenValue, String imgBase64){
        UserDetails userDetails = new UserDetails();
        userDetails.setName(user.getName());
        userDetails.setWx(user.getWx());
        userDetails.setRole(user.getRole());
        userDetails.setDescription(user.getDescription());
        userDetails.setTel(user.getTel());
        userDetails.setStuId(user.getStuId());
        userDetails.setClassName(user.getClassName());
        userDetails.setSaTokenValue(saTokenValue);
        userDetails.setImgBase64("data:image/png;base64," + imgBase64);
        return userDetails;
    }
}
