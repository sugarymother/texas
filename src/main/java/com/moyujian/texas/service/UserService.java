package com.moyujian.texas.service;

import com.moyujian.texas.logic.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service("userService")
public class UserService {

    private static final ConcurrentHashMap<String, User> onlineUserMap = new ConcurrentHashMap<>();

    public void login(User user) {
        onlineUserMap.put(user.getId(), user);
    }

    public void logout(String id) {
        onlineUserMap.remove(id);
    }

    public User getUser(String id) {
        return onlineUserMap.get(id);
    }

    public List<User> getAllUsers() {
        return onlineUserMap.values().stream().toList();
    }
}
