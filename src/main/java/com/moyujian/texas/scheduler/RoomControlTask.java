package com.moyujian.texas.scheduler;

import com.moyujian.texas.service.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RoomControlTask {

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void clearInvalidRoom() {
        RoomService.removeInvalidRooms(log);
    }
}
