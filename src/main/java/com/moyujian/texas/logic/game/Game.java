package com.moyujian.texas.logic.game;

import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.UserStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Game {

    private final String id = UUID.randomUUID().toString().replace("-", "");

    private long updatedTime = System.currentTimeMillis();

    private int accessChipsNum;

    private int minLargeBet;

    private int maxBet;

    private final Deck deck = new Deck();

    private final List<Card> communityArea = new ArrayList<>(5);

    private final List<PlayerArea> playerAreas = new ArrayList<>();

    private int nextPlayerIdx = 0;
    private int nextDealerIdx = 0;

    private PlayerArea currentOpPlayer = null;
    private PlayerArea dealerPlayer = null;
    private PlayerArea endingPlayer = null;

    private int roundBet = 0;

    private int totalBet = 0;

    private int round = 0;

    private boolean gameOver = false;

    private boolean turnEnd = false;

    private static final int FLOP_ROUND = 0;
    private static final int TURN_ROUND = 1;
    private static final int RIVER_ROUND = 2;

    private Game() {}

    public static Game run(List<User> players, GameSetting setting) {
        Game game = new Game();
        // 设置game规则数据
        game.accessChipsNum = setting.getAccessChipsNum();
        game.minLargeBet = setting.getMinLargeBet();
        game.maxBet = setting.getMaxBet();

        // 设置玩家
        // 初始化玩家区域
        for (User player : players) {
            if (UserStatus.ONLINE.equals(player.getStatus())) {
                player.setStatus(UserStatus.GAMING);
            } else {
                player.setStatus(UserStatus.DISCONNECTED);
            }
            player.setGameId(game.id);
            game.playerAreas.add(new PlayerArea(player, game.accessChipsNum));
        }

        return game;
    }

    public void restart() {
        turnEnd = false;

        // 重置所有player
        for (PlayerArea playerArea : playerAreas) {
            playerArea.reset();
        }

        // 洗牌
        deck.shuffle();
        // 发牌
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                playerArea.setHand1(deck.draw());
                playerArea.setHand2(deck.draw());
            }
        }
        // 公共区清空
        communityArea.clear();
        // 重置指针
        dealerPlayer = playerAreas.get(nextDealerIdx);
        currentOpPlayer = dealerPlayer;
        do {
            nextDealerIdx = (nextDealerIdx + 1) % playerAreas.size();
        } while (!playerAreas.get(nextDealerIdx).isAlive());
        nextPlayerIdx = nextDealerIdx;
        endingPlayer = dealerPlayer;

        // 跳过preflop
        round = FLOP_ROUND;
        roundBet = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                playerArea.setBet(minLargeBet);
            }
        }
        totalBet = minLargeBet;
        for (int i = 0, n = 3; i < n; i++) {
            Card drawedCard = deck.draw();
            drawedCard.setTopUp(true);
            communityArea.add(drawedCard);
        }
    }

    private void settle() {
        turnEnd = true;

        int totalChips = 0;
        List<PlayerArea> notFoldPlayers = new ArrayList<>();
        for (PlayerArea playerArea : playerAreas) {
            // 总和池内chips
            totalChips += playerArea.getBet();
            if (playerArea.isAlive() && !playerArea.isFold()) {
                // 未弃牌，则先总合，之后比较结算
                notFoldPlayers.add(playerArea);
            }
        }

        // 未弃牌的翻牌
        if (notFoldPlayers.size() > 1) {
            for (PlayerArea notFoldPlayer : notFoldPlayers) {
                notFoldPlayer.getHand1().setTopUp(true);
                notFoldPlayer.getHand2().setTopUp(true);
            }
        }

        List<PlayerArea> winners = checkWinner(notFoldPlayers);

        int avgChips = totalChips / winners.size();
        int leftChips = totalChips - avgChips;
        winners.sort(Comparator.comparing(PlayerArea::getBet).reversed());
        for (PlayerArea winner : winners) {
            winner.addChips(avgChips + leftChips-- > 0 ? 1 : 0);
        }

        // 不足最小bet则直接强制退出
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive() && playerArea.getChips() < minLargeBet) {
                playerArea.die();
            }
        }

        // check game over
        if (alivePlayerNum() <= 1) {
            gameOver();
        }
    }

    public void inTurnOperate(Operate operate) {
        switch (operate.getOperateType()) {
            case CHECK, CALL -> checkAndCallOperate();
            case RAISE -> raiseOperate(operate.getOperateChips());
            case ALLIN -> allinOperate();
            case FOLD -> foldOperate();
        }
        currentOpPlayer.setLastOperate(operate.getOperateType());
        updatedTime = System.currentTimeMillis();

        if (!playerNext()) {
            // 圈结束
            if (accessiblePlayerNum() <= 1) {
                // 如果全部player无操作，直接结束轮
                settle();
            } else if (!roundNext()) {
                // 轮结束
                settle();
            }
        } else if (accessiblePlayerNum() <= 1) {
            // 只剩最后一个可操作player，直接结束轮
            settle();
        } else if (UserStatus.DISCONNECTED.equals(currentOpPlayer.getUser().getStatus())) {
            // 下个操作player掉线，自动fold
            inTurnOperate(new Operate(OperateType.FOLD));
        }
    }

    public GameSnapshot getSnapshot(User user) {
        GameSnapshot gameSnapshot = new GameSnapshot();
        gameSnapshot.setPlayers(playerAreas.stream()
                .map(GameSnapshot.PlayerSnapshot::fromPlayerArea)
                .toList());
        gameSnapshot.setPublicCards(communityArea.stream()
                .map(GameSnapshot.CardSnapshot::fromCard)
                .toList());
        if (round == FLOP_ROUND) {
            gameSnapshot.setRound("flop round");
        } else if (round == TURN_ROUND) {
            gameSnapshot.setRound("turn round");
        } else if (round == RIVER_ROUND) {
            gameSnapshot.setRound("river round");
        }
        gameSnapshot.setCurrentPlayerIdx(playerAreas.indexOf(currentOpPlayer));

        Optional<PlayerArea> mainPlayerOp = playerAreas.stream()
                .filter(e -> e.getUser().equals(user))
                .findFirst();
        mainPlayerOp.ifPresent(mainPlayer -> gameSnapshot.setMainPlayerIdx(playerAreas.indexOf(mainPlayer)));

        Optional<GameSnapshot.PlayerSnapshot> tarPlayer = gameSnapshot.getPlayers().stream()
                .filter(e -> e.getUserId().equals(user.getId()))
                .findFirst();
        List<GameSnapshot.CardSnapshot> hands = getHands(user);
        if (tarPlayer.isPresent() && hands != null) {
            GameSnapshot.PlayerSnapshot playerSnapshot = tarPlayer.get();
            playerSnapshot.setHand1(hands.get(0));
            playerSnapshot.setHand2(hands.get(1));
        }
        return gameSnapshot;
    }

    private List<GameSnapshot.CardSnapshot> getHands(User user) {
        Optional<PlayerArea> playerOp = playerAreas.stream()
                .filter(e -> e.getUser().equals(user))
                .findFirst();
        if (playerOp.isPresent()) {
            PlayerArea player = playerOp.get();
            List<GameSnapshot.CardSnapshot> hands = new ArrayList<>();
            hands.add(GameSnapshot.CardSnapshot.fromCardTransparent(player.getHand1()));
            hands.add(GameSnapshot.CardSnapshot.fromCardTransparent(player.getHand2()));
            return hands;
        } else {
            return null;
        }
    }

    public List<Operate> getCurrentUserAccessibleOperate() {
        List<Operate> accessibleOperateList = new ArrayList<>();

        int chips = currentOpPlayer.getChips();
        int currBet = currentOpPlayer.getBet();
        int diffBet = totalBet - currBet;

        // 检查是否满足check, call
        if (chips >= diffBet) {
            if (roundBet == 0) {
                // 满足 check
                accessibleOperateList.add(new Operate(OperateType.CHECK, new int[]{diffBet}));
            } else {
                // 满足 call
                accessibleOperateList.add(new Operate(OperateType.CALL, new int[]{diffBet}));
            }

            // 检查是否满足raise
            if (chips >= diffBet + roundBet) {
                List<Integer> accessibleChipsList = new ArrayList<>();
                for (int raiseBet = roundBet; raiseBet + diffBet <= chips && raiseBet <= maxBet; raiseBet += 10) {
                    accessibleChipsList.add(raiseBet + diffBet);
                }
                int[] accessibleChips = accessibleChipsList.stream().mapToInt(Integer::intValue).toArray();
                accessibleOperateList.add(new Operate(OperateType.RAISE, accessibleChips));
            }
        }

        accessibleOperateList.add(new Operate(OperateType.ALLIN, new int[]{chips}));
        accessibleOperateList.add(new Operate(OperateType.FOLD, new int[0]));
        return accessibleOperateList;
    }

    public boolean checkOperate(Operate operate) {
        List<Operate> currentUserAccessibleOperateList = getCurrentUserAccessibleOperate();
        Optional<Operate> operateOp = currentUserAccessibleOperateList.stream()
                .filter(e -> e.getOperateType().equals(operate.getOperateType()))
                .findFirst();
        if (operateOp.isPresent()) {
            if (operateOp.get().getOperateType().equals(OperateType.FOLD)) {
                return true;
            }
            int[] accessibleChips = operateOp.get().getAccessibleChips();
            return Arrays.stream(accessibleChips).anyMatch(e -> e == operate.getOperateChips());
        } else {
            return false;
        }
    }

    public User getCurrentOpUser() {
        return currentOpPlayer.getUser();
    }

    public List<User> getAllNotLeavingPlayers() {
        List<User> users = new ArrayList<>();
        for (PlayerArea playerArea : playerAreas) {
            if (!playerArea.isLeave()) {
                users.add(playerArea.getUser());
            }
        }
        return users;
    }

    public String getId() {
        return id;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void leaveGame(User user) {
        Optional<PlayerArea> playerOp = playerAreas.stream().filter(e -> e.getUser().equals(user)).findFirst();
        if (playerOp.isPresent()) {
            PlayerArea player = playerOp.get();
            if (!player.isAlive()) {
                player.setLeave(true);
            }
        }
    }

    private List<PlayerArea> checkWinner(List<PlayerArea> players) {
        if (players.size() < 2) {
            return players;
        }

        List<PlayerArea> winners = new ArrayList<>();
        int maxWeight;

        for (PlayerArea player : players) {
            CardChecker.check(communityArea, player);
        }
        players.sort(Comparator.comparing(PlayerArea::getCheckResult).reversed());

        maxWeight = players.get(0).getCheckResult().getWeight();
        for (PlayerArea player : players) {
            if (player.getCheckResult().getWeight() == maxWeight) {
                winners.add(player);
                player.setWin(true);
            } else {
                break;
            }
        }
        return winners;
    }

    private boolean playerNext() {
        PlayerArea nextPlayer = playerAreas.get(nextPlayerIdx);
        if (nextPlayer.equals(endingPlayer)) {
            return false;
        }
        currentOpPlayer = nextPlayer;
        nextPlayerIdx = (nextPlayerIdx + 1) % playerAreas.size();

        if (currentOpPlayer.isAlive() && !currentOpPlayer.isAllin() && !currentOpPlayer.isFold()) {
            return true;
        } else {
            return playerNext();
        }
    }

    private boolean roundNext() {
        roundBet = 0;
        endingPlayer = dealerPlayer;
        return ++round <= RIVER_ROUND;
    }

    private void checkAndCallOperate() {
        currentOpPlayer.placeBet(totalBet - currentOpPlayer.getBet());
    }

    private void raiseOperate(int bet) {
        int raiseBet = bet + currentOpPlayer.getBet() - totalBet;
        roundBet += raiseBet;
        currentOpPlayer.placeBet(bet);
        totalBet = currentOpPlayer.getBet();
        endingPlayer = currentOpPlayer;
    }

    private void foldOperate() {
        currentOpPlayer.setFold(true);
    }

    private void allinOperate() {
        int raiseBet = currentOpPlayer.getBet() + currentOpPlayer.getChips() - totalBet;
        if (raiseBet > 0) {
            roundBet += raiseBet;
            totalBet += raiseBet;
        }
        currentOpPlayer.placeBet(currentOpPlayer.getChips());
        currentOpPlayer.setAllin(true);
    }

    private void gameOver() {
        gameOver = true;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                playerArea.getUser().earn(playerArea.getChips());
            }
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isTurnEnd() {
        return turnEnd;
    }

    private int alivePlayerNum() {
        int alivePlayerNum = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive()) {
                alivePlayerNum++;
            }
        }
        return alivePlayerNum;
    }

    private int accessiblePlayerNum() {
        int accessiblePlayerNum = 0;
        for (PlayerArea playerArea : playerAreas) {
            if (playerArea.isAlive() && !playerArea.isFold() && !playerArea.isAllin()) {
                accessiblePlayerNum++;
            }
        }
        return accessiblePlayerNum;
    }
}
