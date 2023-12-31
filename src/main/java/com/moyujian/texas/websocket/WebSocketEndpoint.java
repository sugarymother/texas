package com.moyujian.texas.websocket;

import com.moyujian.texas.constants.ResponseStatus;
import com.moyujian.texas.constants.WsOperateType;
import com.moyujian.texas.exception.UnexpectedTypeException;
import com.moyujian.texas.logic.UserStatus;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.game.GameSnapshot;
import com.moyujian.texas.logic.game.OperateType;
import com.moyujian.texas.request.GameOperateRequest;
import com.moyujian.texas.request.OperateModel;
import com.moyujian.texas.request.RoomOperateModel;
import com.moyujian.texas.request.RoomOperateRequest;
import com.moyujian.texas.request.WsRequest;
import com.moyujian.texas.response.WsResponse;
import com.moyujian.texas.service.GameService;
import com.moyujian.texas.service.RoomService;
import com.moyujian.texas.service.UserService;
import com.moyujian.texas.utils.JsonConvertUtil;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint("/ws/{series}")
@Component
@Slf4j
public class WebSocketEndpoint {

    private static final Map<String, WebSocketEndpoint> ENDPOINT_MAP = new ConcurrentHashMap<>();
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);

    private Session session;

    private User user;

    @OnOpen
    public void onOpen(@PathParam("series") String series , Session session) throws IOException {
        this.session=session;
        user = UserService.getUserBySeries(series);
        log.info("[ws] new session opened, session：{}, user: {}", session.getId(), JsonConvertUtil.toJSON(user));

        List<WebSocketEndpoint> oldEndpoints = ENDPOINT_MAP.values().stream()
                .filter(e -> e.user.equals(user))
                .toList();
        for (WebSocketEndpoint oldEndpoint : oldEndpoints) {
            oldEndpoint.session.close(
                    new CloseReason(CloseReason.CloseCodes.GOING_AWAY, ResponseStatus.OLD_CONNECT_ABORT.getMsg()));
        }
        ENDPOINT_MAP.put(session.getId(), this);
        ONLINE_COUNT.incrementAndGet();

        if (UserStatus.DISCONNECTED.equals(user.getStatus())) {
            user.setStatus(UserStatus.GAMING);
            sendMessage(new WsResponse<>(WsOperateType.RECONNECT_INTO_GAME));
        } else if (UserStatus.OFFLINE.equals(user.getStatus())) {
            user.setStatus(UserStatus.ONLINE);
            sendMessage(new WsResponse<>(WsOperateType.FLUSH_USER_LIST));
        }
    }

    @OnMessage
    public void onMessage(String message, Session session)
            throws IOException, UnexpectedTypeException, InterruptedException {
        log.debug("[ws] msg received, session: {}, msg: {}, user: {}",
                session.getId(), message, JsonConvertUtil.toJSON(user));
        WsRequest request = JsonConvertUtil.fromJSON(message, WsRequest.class);
        WsOperateType wsOperateType = WsOperateType.getBySerial(request.getType());
        if (wsOperateType == null) {
            log.error("[ws] ws msg type is unexpected, session: {} type: {}", session.getId(), request.getType());
            return;
        }
        switch (wsOperateType) {
            case OPERATE -> {
                GameOperateRequest gameOperateRequest = JsonConvertUtil.fromJSON(message, GameOperateRequest.class);
                GameService.operate(user, gameOperateRequest.getData());
            }
            case GET_GAME_SNAPSHOT -> {
                GameSnapshot gameSnapshot = GameService.getGameSnapshot(user);
                sendMessage(new WsResponse<>(WsOperateType.FLUSH_GAME_SNAPSHOT, gameSnapshot));
            }
            case LEAVE_AFTER_DIE -> GameService.leaveGame(user);
            case OPEN_GAME -> {
                RoomOperateRequest roomOperateRequest = JsonConvertUtil.fromJSON(message, RoomOperateRequest.class);
                RoomService.startGame(roomOperateRequest.getData().getRoomId(), user);
            }
            case ENTER_ROOM -> RoomService.enter(user);
            case LEAVE_ROOM -> RoomService.leave(user);
        }
    }

    @OnError
    public void onError(Throwable error, Session session) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        log.error("[ws] error occurred, session: {}, user: {} exception: {}\n{}",
                session.getId(), JsonConvertUtil.toJSON(user), error.getMessage(), stringWriter);
        session.close(new CloseReason(
                CloseReason.CloseCodes.UNEXPECTED_CONDITION, ResponseStatus.ERROR_OCCURRED.getMsg()));
    }

    @OnClose
    public void onClose() {
        ENDPOINT_MAP.remove(session.getId());
        ONLINE_COUNT.decrementAndGet();
        log.info("[ws] session closed, session: {}, user: {}", session.getId(), JsonConvertUtil.toJSON(user));

        if (user != null) {
            if (UserStatus.GAMING.equals(user.getStatus())) {
                user.setStatus(UserStatus.DISCONNECTED);

                if (user.equals(GameService.getCurrentOperateUser(user.getGameId()))) {
                    OperateModel operateModel = new OperateModel();
                    operateModel.setType(OperateType.FOLD.getSerial());
                    try {
                        GameService.operate(user, operateModel);
                    } catch (UnexpectedTypeException | IOException | InterruptedException ignore) {}
                }
            } else {
                user.setStatus(UserStatus.OFFLINE);
                if (user.getRoomId() != null) {
                    try {
                        RoomService.leave(user);
                    } catch (IOException ignore) {}
                }
            }
        }
    }

    private void sendMessage(Object vo) throws IOException {
        String msg = JsonConvertUtil.toJSON(vo);
        session.getBasicRemote().sendText(msg);
        // TODO 考虑发送失败的情况
        log.debug("[ws] msg sent, session: {}, user: {}, msg: {}",
                session.getId(), JsonConvertUtil.toJSON(user), msg);
    }

    public static synchronized void sendMessageByUsers(Object vo, List<User> users) throws IOException {
        List<WebSocketEndpoint> endpointList = ENDPOINT_MAP.values().stream()
                .filter(e -> users.contains(e.user))
                .toList();
        for (WebSocketEndpoint endpoint : endpointList) {
            endpoint.sendMessage(vo);
        }
    }

    public static synchronized void sendMessageByUserStatus(Object vo, UserStatus... status) throws IOException {
        List<UserStatus> statusList = List.of(status);
        List<WebSocketEndpoint> endpointList = ENDPOINT_MAP.values().stream()
                .filter(e -> statusList.contains(e.user.getStatus()))
                .toList();
        for (WebSocketEndpoint endpoint : endpointList) {
            endpoint.sendMessage(vo);
        }
    }
}
