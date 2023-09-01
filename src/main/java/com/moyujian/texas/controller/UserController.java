package com.moyujian.texas.controller;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.constants.ResponseStatus;
import com.moyujian.texas.constants.UserStatus;
import com.moyujian.texas.exception.TokenVerifyException;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.request.UserRequest;
import com.moyujian.texas.response.CommonResponse;
import com.moyujian.texas.response.UserVo;
import com.moyujian.texas.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sign", consumes = {"application/json"})
    public CommonResponse<UserVo> sign(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        String username = userRequest.getUsername();
        if (Strings.isEmpty(username)) {
            username = "用户" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        }
        User user = User.createNew(username, Constants.DEFAULT_INIT_CHIPS);
        Cookie cookie = new Cookie(Constants.COOKIE_NAME, user.signToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/refresh")
    public CommonResponse<UserVo> refresh(@CookieValue(value = Constants.COOKIE_NAME, required = false) String token,
                                          HttpServletResponse response) {
        if (Strings.isEmpty(token)) {
            // 无token，说明还未拥有用户信息，返回信息使前端走创建用户流程
            return CommonResponse.get(ResponseStatus.NOT_SINGED);
        }
        String id = User.getIdFromToken(token);
        User user = userService.getUser(id);
        if (user != null) {
            // 线上存在此用户
            response.addCookie(new Cookie(Constants.COOKIE_NAME, user.signToken()));
            if (UserStatus.DISCONNECTED.equals(user.getStatus())) {
                // 说明用户正在game中，返回信息使前端走重连流程
                return CommonResponse.get(ResponseStatus.IN_GAME, UserVo.fromUser(user));
            } else {
                return CommonResponse.suc(UserVo.fromUser(user));
            }
        } else {
            // 线上无此用户，返回信息使前端走登录流程
            try {
                user = User.createFromToken(token);
            } catch (TokenVerifyException e) {
                log.warn("user token verify failed: {}", token);
                return CommonResponse.err();
            }
            response.addCookie(new Cookie(Constants.COOKIE_NAME, user.signToken()));
            return CommonResponse.get(ResponseStatus.NOT_LOGIN, UserVo.fromUser(user));
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/recharge")
    public CommonResponse<UserVo> recharge(@CookieValue(Constants.COOKIE_NAME) String token,
                                           HttpServletResponse response) {
        String id = User.getIdFromToken(token);
        User user = userService.getUser(id);
        if (user == null) {
            return CommonResponse.get(ResponseStatus.NOT_LOGIN);
        }
        user.recharge(Constants.DEFAULT_RECHARGE_CHIPS);
        response.addCookie(new Cookie(Constants.COOKIE_NAME, user.signToken()));
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/list")
    public CommonResponse<List<UserVo>> listOnline() {
        List<User> userList = userService.getAllUsers();
        List<UserVo> userVoList = new ArrayList<>();
        userList.forEach(e -> userVoList.add(UserVo.fromUser(e)));
        userVoList.sort(Comparator.comparingInt(UserVo::getEarnedChips));
        return CommonResponse.suc(userVoList);
    }
}
