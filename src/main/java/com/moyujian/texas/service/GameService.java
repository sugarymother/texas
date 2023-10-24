package com.moyujian.texas.service;

import com.moyujian.texas.constants.WsOperateType;
import com.moyujian.texas.exception.UnexpectedTypeException;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.UserStatus;
import com.moyujian.texas.logic.game.Game;
import com.moyujian.texas.logic.game.GameSetting;
import com.moyujian.texas.logic.game.GameSnapshot;
import com.moyujian.texas.logic.game.Operate;
import com.moyujian.texas.logic.game.OperateType;
import com.moyujian.texas.request.OperateModel;
import com.moyujian.texas.response.WsResponse;
import com.moyujian.texas.utils.JsonConvertUtil;
import com.moyujian.texas.websocket.WebSocketEndpoint;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GameService {

    private static final long OPERATE_DELAY = 4000L;

    private static final long GAME_MAX_LIFE = 10 * 60 * 1000L;

    private static final ConcurrentHashMap<String, Game> GAME_MAP = new ConcurrentHashMap<>();

    public static void openGame(List<User> players, GameSetting gameSetting) throws IOException, InterruptedException {
        Game game = Game.run(players, gameSetting);
        GAME_MAP.put(game.getId(), game);

        sendGameStartToAllPlayers(game.getId());
        Thread.sleep(OPERATE_DELAY);
        game.restart();
        sendGameSnapshotToAllPlayers(game.getId());
        sendOperateToCurrentPlayer(game.getId());
    }

    public static void operate(User user, OperateModel operateModel) throws UnexpectedTypeException, IOException {
        if (!GAME_MAP.containsKey(user.getGameId())) {
            return;
        }
        Game game = GAME_MAP.get(user.getGameId());

        OperateType operateType = OperateType.getBySerial(operateModel.getType());
        if (operateType == null) {
            throw new UnexpectedTypeException("operate type serial: " + operateModel.getType() + " is unexpected.");
        }
        Operate operate = new Operate(operateType, operateModel.getChipNum());
        if (!game.checkOperate(operate)) {
            throw new UnexpectedTypeException("operate: " + JsonConvertUtil.toJSON(operate) + " is unexpected.");
        }

        game.inTurnOperate(operate);
        if (game.isGameOver()) {
            sendGameOverToAllPlayers(game.getId());

            for (User player : game.getAllNotLeavingPlayers()) {
                player.setGameId(null);
                if (UserStatus.GAMING.equals(player.getStatus())) {
                    player.setStatus(UserStatus.ONLINE);
                } else if (UserStatus.DISCONNECTED.equals(player.getStatus())) {
                    player.setStatus(UserStatus.OFFLINE);
                }
            }

            GAME_MAP.remove(game.getId());
        } else if (game.isTurnEnd()) {
            sendTurnOverToAllPlayers(game.getId());
        } else {
            sendOperateToCurrentPlayer(game.getId());
        }
    }

    public static GameSnapshot getGameSnapshot(User user) {
        Game game = GAME_MAP.get(user.getGameId());
        if (game == null) {
            return null;
        }
        return game.getSnapshot(user);
    }

    public static User getCurrentOperateUser(String gameId) {
        if (!GAME_MAP.containsKey(gameId)) {
            return null;
        } else {
            return GAME_MAP.get(gameId).getCurrentOpUser();
        }
    }

    public static void leaveGame(User user) {
        Game game = GAME_MAP.get(user.getGameId());
        if (game == null) {
            return;
        }
        game.leaveGame(user);
    }

    public static void removeInvalidGames(Logger logger) {
        List<Game> toRemoveGames = new ArrayList<>();
        for (Map.Entry<String, Game> entry : GAME_MAP.entrySet()) {
            Game game = entry.getValue();
            if (System.currentTimeMillis() - game.getUpdatedTime() > GAME_MAX_LIFE) {
                toRemoveGames.add(game);
            }
        }
        for (Game game : toRemoveGames) {
            GAME_MAP.remove(game.getId());
            logger.debug("running game removed, game id: {}", game.getId());
        }
    }

    private static void sendGameSnapshotToAllPlayers(String gameId) throws IOException {
        if (!GAME_MAP.containsKey(gameId)) {
            return;
        }
        Game game = GAME_MAP.get(gameId);
        for (User player : game.getAllNotLeavingPlayers()) {
            WebSocketEndpoint.sendMessageByUsers(
                    new WsResponse<>(WsOperateType.FLUSH_GAME_SNAPSHOT, game.getSnapshot(player)),
                    List.of(player));
        }
    }

    private static void sendOperateToCurrentPlayer(String gameId) throws IOException {
        if (!GAME_MAP.containsKey(gameId)) {
            return;
        }
        Game game = GAME_MAP.get(gameId);
        WebSocketEndpoint.sendMessageByUsers(
                new WsResponse<>(WsOperateType.IN_TURN_OPERATE, game.getCurrentUserAccessibleOperate()),
                List.of(game.getCurrentOpUser()));
    }

    private static void sendGameStartToAllPlayers(String gameId) throws IOException {
        if (!GAME_MAP.containsKey(gameId)) {
            return;
        }
        Game game = GAME_MAP.get(gameId);
        WebSocketEndpoint.sendMessageByUsers(
                new WsResponse<>(WsOperateType.GAME_START),
                game.getAllNotLeavingPlayers());
    }

    private static void sendGameOverToAllPlayers(String gameId) throws IOException {
        if (!GAME_MAP.containsKey(gameId)) {
            return;
        }
        Game game = GAME_MAP.get(gameId);
        for (User player : game.getAllNotLeavingPlayers()) {
            WebSocketEndpoint.sendMessageByUsers(
                    new WsResponse<>(WsOperateType.GAME_OVER, game.getSnapshot(player)),
                    List.of(player));
        }
    }

    private static void sendTurnOverToAllPlayers(String gameId) throws IOException {
        if (!GAME_MAP.containsKey(gameId)) {
            return;
        }
        Game game = GAME_MAP.get(gameId);
        for (User player : game.getAllNotLeavingPlayers()) {
            WebSocketEndpoint.sendMessageByUsers(
                    new WsResponse<>(WsOperateType.TURN_OVER, game.getSnapshot(player)),
                    List.of(player));
        }
    }
}
