package com.moyujian.texas.logic.game;

import com.moyujian.texas.logic.User;
import com.moyujian.texas.logic.game.Card;
import lombok.Data;

@Data
public class PlayerArea {

    private User user;

    private int chips;

    private int bet;

    private Card hand1;

    private Card hand2;
}
