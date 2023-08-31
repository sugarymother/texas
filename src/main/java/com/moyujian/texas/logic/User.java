package com.moyujian.texas.logic;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.constants.UserStatus;
import com.moyujian.texas.exception.TokenVerifyException;
import lombok.Data;

import java.util.UUID;

@Data
public class User {

    private String id;

    private String username;

    private int chips;

    private UserStatus status = UserStatus.ONLINE;

    private User() {}

    public String signToken() {
        return JWT.create()
                .withClaim("id", id)
                .withClaim("username", username)
                .withClaim("chips", chips)
                .withIssuer(Constants.ISSUE_NAME)
                .sign(Algorithm.HMAC256(Constants.JWT_KEY));
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
        return user;
    }
}
