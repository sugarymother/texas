package com.moyujian.texas.logic;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
public class RoomSnapshot {

    private String id;

    private List<UserSnapshot> users;

    private int accessChipsNum;

    private String minLargeBet;

    private int maxBet;

    private int mainUserIdx;

    @Data
    public static class UserSnapshot {
        private String username;
        private int chips;
        private int rechargeTimes;
        private boolean isOwner = false;

        public boolean getIsOwner() {
            return isOwner;
        }

        public void setIsOwner(boolean owner) {
            isOwner = owner;
        }

        public static UserSnapshot fromUser(User user) {
            UserSnapshot userSnapshot = new UserSnapshot();
            BeanUtils.copyProperties(user, userSnapshot);
            return userSnapshot;
        }
    }
}
