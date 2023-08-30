package com.moyujian.texas.response;

import com.moyujian.texas.logic.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserVo {

    private String id;

    private String username;

    private int chips;

    public static UserVo fromUser(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }
}
