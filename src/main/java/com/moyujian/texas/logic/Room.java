package com.moyujian.texas.logic;

import com.moyujian.texas.logic.game.GameSetting;
import com.moyujian.texas.logic.game.PlayerArea;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class Room {

    private static final int ROOM_MAX_CAP = 8;

    private final String id = UUID.randomUUID().toString().replace("-", "");

    private long updatedTime = System.currentTimeMillis();

    private final LinkedList<User> userList = new LinkedList<>();

    private User roomOwner;

    private GameSetting gameSetting;

    private boolean empty = false;

    private Room() {}

    public static Room create(User owner, GameSetting gameSetting) {
        Room room = new Room();
        room.roomOwner = owner;
        room.userList.add(owner);
        room.gameSetting = gameSetting;
        owner.setRoomId(room.id);
        return room;
    }

    public boolean enter(User user) {
        if (userList.size() >= ROOM_MAX_CAP) {
            return false;
        }
        if (empty) {
            empty = false;
            roomOwner = user;
        }
        userList.addLast(user);
        updatedTime = System.currentTimeMillis();
        user.setRoomId(id);
        return true;
    }

    public void leave(User user) {
        userList.remove(user);
        if (userList.size() == 0) {
            empty = true;
            roomOwner = null;
        } else if (user.equals(roomOwner)) {
            roomOwner = userList.getFirst();
        }
        updatedTime = System.currentTimeMillis();
        user.setRoomId(null);
    }

    public RoomSnapshot getSnapshot(User user) {
        RoomSnapshot roomSnapshot = new RoomSnapshot();
        roomSnapshot.setId(id);
        roomSnapshot.setUsers(userList.stream().map(RoomSnapshot.UserSnapshot::fromUser).toList());
        roomSnapshot.setAccessChipsNum(gameSetting.getAccessChipsNum());
        roomSnapshot.setMaxBet(gameSetting.getMaxBet());
        roomSnapshot.setMinLargeBet(gameSetting.getMinLargeBet());
        roomSnapshot.setOwnerIdx(userList.indexOf(roomOwner));
        roomSnapshot.setMainUserIdx(userList.indexOf(user));
        roomSnapshot.setOwner(user.equals(roomOwner));
        return roomSnapshot;
    }
}
