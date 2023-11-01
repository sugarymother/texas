package com.moyujian.texas.service;

import com.moyujian.texas.constants.WsOperateType;
import com.moyujian.texas.logic.Room;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.game.GameSetting;
import com.moyujian.texas.response.WsResponse;
import com.moyujian.texas.websocket.WebSocketEndpoint;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RoomService {

    private static final long ROOM_MAX_LIFE = 30 * 60 * 1000L;

    private static final Map<String, Room> ROOM_MAP = new HashMap<>();

    private static final Queue<Room> ROOM_QUEUE = new LinkedList<>();

    public static synchronized void enter(User user) throws IOException {
        Room room = null;
        if (ROOM_QUEUE.isEmpty()) {
            room = createNewRoom(user);
        } else {
            for (Room queRoom : ROOM_QUEUE) {
                if (queRoom.enter(user)) {
                    room = queRoom;
                    break;
                }
            }
            if (room == null) {
                room = createNewRoom(user);
            }
        }

        sendRoomSnapshotToAllPlayers(room.getId());
    }

    public static synchronized void leave(User user) throws IOException {
        Room room = ROOM_MAP.get(user.getRoomId());
        if (room == null) {
            return;
        }
        room.leave(user);
        if (room.isEmpty()) {
            removeRoom(room.getId());
        } else {
            sendRoomSnapshotToAllPlayers(room.getId());
        }
    }

    public static synchronized void startGame(String roomId, User user) throws IOException, InterruptedException {
        Room room = ROOM_MAP.get(roomId);
        if (room == null || !room.getRoomOwner().equals(user)) {
            return;
        }
        removeRoom(roomId);
        GameService.openGame(room.getUserList(), room.getGameSetting());
    }

    public static synchronized void removeInvalidRooms(Logger logger) {
        List<Room> toRemoveRooms = new ArrayList<>();
        for (Room room : ROOM_QUEUE) {
            if (room.isEmpty() || System.currentTimeMillis() -  room.getUpdatedTime() > ROOM_MAX_LIFE) {
                toRemoveRooms.add(room);
            }
        }
        for (Room room : toRemoveRooms) {
            removeRoom(room.getId());
            logger.debug("invalid room removed, room id: {}", room.getId());
        }
    }

    private static Room createNewRoom(User owner) {
        Room room = Room.create(owner, defaultGameSetting());
        ROOM_MAP.put(room.getId(), room);
        ROOM_QUEUE.offer(room);
        return room;
    }

    private static void removeRoom(String roomId) {
        Room room = ROOM_MAP.get(roomId);
        if (room == null) {
            return;
        }
        if (room.equals(ROOM_QUEUE.peek())) {
            do {
                ROOM_MAP.remove(ROOM_QUEUE.poll().getId());
            } while (!ROOM_QUEUE.isEmpty() && ROOM_QUEUE.peek().isEmpty());
        } else {
            ROOM_QUEUE.remove(room);
            ROOM_MAP.remove(room.getId());
        }
    }

    private static GameSetting defaultGameSetting() {
        return new GameSetting();
    }

    private static void sendRoomSnapshotToAllPlayers(String roomId) throws IOException {
        if (!ROOM_MAP.containsKey(roomId)) {
            return;
        }
        Room room = ROOM_MAP.get(roomId);
        if (room.isEmpty()) {
            return;
        }
        for (User user : room.getUserList()) {
            WebSocketEndpoint.sendMessageByUsers(
                    new WsResponse<>(WsOperateType.FLUSH_ROOM_SNAPSHOT, room.getSnapshot(user)), List.of(user));
        }
    }
}
