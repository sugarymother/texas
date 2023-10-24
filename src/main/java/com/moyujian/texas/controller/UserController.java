package com.moyujian.texas.controller;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.constants.ResponseStatus;
import com.moyujian.texas.exception.TokenVerifyException;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.request.UserRequest;
import com.moyujian.texas.response.CommonResponse;
import com.moyujian.texas.response.UserListVo;
import com.moyujian.texas.response.UserVo;
import com.moyujian.texas.service.UserService;
import com.moyujian.texas.utils.CookieUtil;
import com.moyujian.texas.utils.IpUtil;
import com.moyujian.texas.utils.Md5Util;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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

    @RequestMapping(method = RequestMethod.POST, path = "/sign", consumes = {"application/json"})
    public CommonResponse<UserVo> sign(@RequestBody UserRequest userRequest,
                                       HttpServletRequest request, HttpServletResponse response) {
        String username = userRequest.getUsername();
        if (Strings.isEmpty(username)) {
            username = "用户" + UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        }
        User user = User.createNew(username, Constants.DEFAULT_INIT_CHIPS);
        UserService.login(user);
        user.setOnlineSeries(Md5Util.getMd5Hex(IpUtil.getIp(request) + System.currentTimeMillis()));

        CookieUtil.setCookie(user, response);
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/refresh")
    public CommonResponse<UserVo> refresh(@CookieValue(value = Constants.COOKIE_NAME, required = false) String token,
                                          HttpServletRequest request, HttpServletResponse response) {
        if (Strings.isEmpty(token)) {
            // 无token，说明还未拥有用户信息，返回信息使前端走创建用户流程
            return CommonResponse.get(ResponseStatus.NOT_SINGED);
        }
        String id = User.getIdFromToken(token);
        User user = UserService.getUser(id);
        if (user == null)  {
            // 线上无此用户
            try {
                user = User.createFromToken(token);
            } catch (TokenVerifyException e) {
                log.warn("user token verify failed, token: {}", token);
                return CommonResponse.err();
            }
            UserService.login(user);
        }
        user.setOnlineSeries(Md5Util.getMd5Hex(IpUtil.getIp(request) + System.currentTimeMillis()));

        CookieUtil.setCookie(user, response);
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/recharge")
    public CommonResponse<UserVo> recharge(@CookieValue(Constants.COOKIE_NAME) String token,
                                           HttpServletResponse response) {
        String id = User.getIdFromToken(token);
        User user = UserService.getUser(id);
        if (user == null) {
            return CommonResponse.get(ResponseStatus.NOT_CONNECTED);
        }
        user.recharge(Constants.DEFAULT_RECHARGE_CHIPS);

        CookieUtil.setCookie(user, response);
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/list")
    public CommonResponse<List<UserListVo>> listOnlineUsers() {
        List<User> userList = UserService.getAllUsers();
        List<UserListVo> userVoList = new ArrayList<>();
        userList.forEach(e -> userVoList.add(UserListVo.fromUser(e)));
        userVoList.sort(Comparator.comparingInt(UserListVo::getEarnedChips));
        return CommonResponse.suc(userVoList);
    }
}
