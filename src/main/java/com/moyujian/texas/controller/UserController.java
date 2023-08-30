package com.moyujian.texas.controller;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.constants.Responses;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.request.UserRequest;
import com.moyujian.texas.response.CommonResponse;
import com.moyujian.texas.response.UserVo;
import com.moyujian.texas.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/sign", consumes = {"application/json"})
    public CommonResponse<UserVo> sign(@RequestBody UserRequest userRequest, HttpServletResponse response) {
        String username = userRequest.getUsername();
        if (Strings.isEmpty(username)) {
            username = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
        User user = User.createNew(username, Constants.DEFAULT_INIT_CHIPS);
        Cookie cookie = new Cookie(Constants.COOKIE_NAME, user.signToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return CommonResponse.suc(UserVo.fromUser(user));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/refresh", consumes = {"application/json"})
    public CommonResponse<UserVo> refresh(@CookieValue(value = Constants.COOKIE_NAME, required = false) String token,
                                          HttpServletResponse response) {
        if (Strings.isEmpty(token)) {
            // 无token，说明还未拥有用户信息，返回信息使前端走创建用户流程
            return CommonResponse.get(Responses.NOT_SINGED);
        }
        String id = User.getIdFromToken(token);
        User user = userService.getUser(id);
        if (user != null) {
            // 线上存在此用户，说明用户正在game中，返回信息使前端走重连流程
            response.addCookie(new Cookie(Constants.COOKIE_NAME, user.signToken()));
            return CommonResponse.get(Responses.IN_GAME);
        }
        // 线上无此用户，用户登录到线上
        user = User.createFromToken(token);
        userService.login(user);
        response.addCookie(new Cookie(Constants.COOKIE_NAME, user.signToken()));
        return CommonResponse.suc(UserVo.fromUser(user));
    }
}
