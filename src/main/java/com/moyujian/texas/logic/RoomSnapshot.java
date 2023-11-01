package com.moyujian.texas.logic;

import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class RoomSnapshot {

    private String id;

    private List<UserSnapshot> users;

    private int accessChipsNum;

    private int minLargeBet;

    private int maxBet;

    private int ownerIdx;

    private int mainUserIdx;

    private boolean isOwner;

    @Data
    public static class UserSnapshot {
        private String username;
        private int chips;
        private int rechargeTimes;

        public static UserSnapshot fromUser(User user) {
            UserSnapshot userSnapshot = new UserSnapshot();
            BeanUtils.copyProperties(user, userSnapshot);
            return userSnapshot;
        }
    }
}
