package com.moyujian.texas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.logic.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserVo {

    private String id;

    private String username;

    private int chips;

    private int rechargeTimes;

    private int earnedChips;

    private String status;

    public static UserVo fromUser(User user) {
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.earnedChips = userVo.chips
                - userVo.rechargeTimes * Constants.DEFAULT_RECHARGE_CHIPS
                - Constants.DEFAULT_INIT_CHIPS;
        userVo.status = user.getStatus().getStatus();
        return userVo;
    }
}
