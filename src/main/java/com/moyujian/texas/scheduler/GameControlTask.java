package com.moyujian.texas.scheduler;

import com.moyujian.texas.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class GameControlTask {

    @Scheduled(initialDelay = 10, fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void clearInvalidGame() {
        GameService.removeInvalidGames(log);
    }
}
