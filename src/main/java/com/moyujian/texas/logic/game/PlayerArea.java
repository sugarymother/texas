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

    private Card hand1 = null;

    private Card hand2 = null;

    public PlayerArea(User user, int accessChipsNum) {
        this.user = user;
        chips = accessChipsNum;
        this.user.consume(accessChipsNum);
    }
}
