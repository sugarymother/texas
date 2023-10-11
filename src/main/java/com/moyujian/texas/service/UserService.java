package com.moyujian.texas.service;

import com.moyujian.texas.logic.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {

    private static final ConcurrentHashMap<String, User> USER_MAP = new ConcurrentHashMap<>();

    public static void login(User user) {
        USER_MAP.put(user.getId(), user);
    }

    public static void remove(String id) {
        USER_MAP.remove(id);
    }

    public static User getUser(String id) {
        return USER_MAP.get(id);
    }

    public static User getUserBySeries(String series) {
        return USER_MAP.values().stream()
                .filter(e -> e.getOnlineSeries().equals(series))
                .findFirst().orElse(null);
    }

    public static List<User> getAllUsers() {
        return USER_MAP.values().stream().toList();
    }
}
