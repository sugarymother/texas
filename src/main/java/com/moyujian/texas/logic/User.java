package com.moyujian.texas.logic;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.exception.TokenVerifyException;
import lombok.Data;

import java.util.UUID;

@Data
public class User {

    private String id;

    private String username;

    private int chips;

    private int rechargeTimes;

    private UserStatus status = UserStatus.OFFLINE;

    private long statusUpdatedTime;

    private String onlineSeries;

    private String gameId;

    private String roomId;

    private User() {}

    public String signToken() {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("chips", chips)
                .withClaim("rechargeTimes", rechargeTimes)
                .withIssuer(Constants.ISSUE_NAME)
                .sign(Algorithm.HMAC256(Constants.JWT_KEY));
    }

    public void recharge(int chipsToRecharge) {
        chips += chipsToRecharge;
        rechargeTimes++;
    }

    public void consume(int chipsToConsume) {
        chips -= chipsToConsume;
    }

    public void earn(int chipsToEarn) {
        chips += chipsToEarn;
    }

    public synchronized void setStatus(UserStatus status) {
        this.status = status;
        statusUpdatedTime = System.currentTimeMillis();
    }

    public synchronized void setOnlineSeries(String onlineSeries) {
        this.onlineSeries = onlineSeries;
        statusUpdatedTime = System.currentTimeMillis();
    }

    private static final JWTVerifier VERIFIER = JWT.require(Algorithm.HMAC256(Constants.JWT_KEY)).build();

    public static User createFromToken(String token) throws TokenVerifyException {
        DecodedJWT verified;
        try {
            verified = VERIFIER.verify(token);
        } catch (JWTVerificationException e) {
            throw new TokenVerifyException(e);
        }
        User user = new User();
        user.id = verified.getClaim("id").asString();
        user.username = verified.getClaim("username").asString();
        user.chips = verified.getClaim("chips").asInt();
        user.rechargeTimes = verified.getClaim("rechargeTimes").asInt();
        return user;
    }

    public static String getIdFromToken(String token) {
        return JWT.decode(token).getClaim("id").asString();
    }

    public static User createNew(String username, int chips) {
        User user = new User();
        user.id = UUID.randomUUID().toString().replace("-", "");
        user.username = username;
        user.chips = chips;
        user.rechargeTimes = 0;
        return user;
    }
}
