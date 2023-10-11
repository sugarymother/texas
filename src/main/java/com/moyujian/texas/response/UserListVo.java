package com.moyujian.texas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.logic.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserListVo {
    private String id;

    private String username;

    private int chips;

    private int rechargeTimes;

    private int earnedChips;

    private String status;

    public static UserListVo fromUser(User user) {
        UserListVo userListVo = new UserListVo();
        BeanUtils.copyProperties(user, userListVo);
        userListVo.earnedChips = userListVo.chips
                - userListVo.rechargeTimes * Constants.DEFAULT_RECHARGE_CHIPS
                - Constants.DEFAULT_INIT_CHIPS;
        userListVo.status = user.getStatus().getStatus();
        return userListVo;
    }
}
