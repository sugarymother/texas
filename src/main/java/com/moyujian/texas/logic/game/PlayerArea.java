package com.moyujian.texas.logic.game;

import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.game.Card;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerArea {

    private User user;

    private int chips;

    private int bet = 0;

    private boolean fold = false;

    private boolean allin = false;

    private boolean alive = true;

    private boolean leave = false;

    private Card hand1 = null;

    private Card hand2 = null;

    private OperateType lastOperate = null;

    private CardChecker.Result checkResult = null;

    private boolean win = false;

    public PlayerArea(User user, int accessChipsNum) {
        this.user = user;
        chips = accessChipsNum;
        this.user.consume(accessChipsNum);
    }

    public void placeBet(int bet) {
        this.bet += bet;
        chips -= bet;
    }

    public void addChips(int chips) {
        this.chips += chips;
    }

    public void reset() {
        bet = 0;
        fold = false;
        allin = false;
        hand1 = null;
        hand2 = null;
        lastOperate = null;
        checkResult = null;
        win = false;
    }
}
