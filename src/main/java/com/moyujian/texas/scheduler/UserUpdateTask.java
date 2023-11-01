package com.moyujian.texas.scheduler;

import com.moyujian.texas.constants.WsOperateType;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.UserStatus;
import com.moyujian.texas.response.WsResponse;
import com.moyujian.texas.service.UserService;
import com.moyujian.texas.utils.JsonConvertUtil;
import com.moyujian.texas.websocket.WebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class UserUpdateTask {

    private static final long MAX_OFFLINE_TIME_MILLIS = 60L * 60L * 1000L;

    @Scheduled(initialDelay = 30, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void clearOfflineUser() {
        for (User user : UserService.getAllUsers()) {
            if (UserStatus.OFFLINE.equals(user.getStatus())
                    && (System.currentTimeMillis() - user.getStatusUpdatedTime() > MAX_OFFLINE_TIME_MILLIS)) {
                UserService.remove(user.getId());
                log.debug("cached user removed, user: {}", JsonConvertUtil.toJSON(user));
            }
        }
    }

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    public void notifyUpdateUserList() throws IOException {
        WebSocketEndpoint.sendMessageByUserStatus(
                new WsResponse<>(WsOperateType.FLUSH_USER_LIST), UserStatus.ONLINE);
    }
}
