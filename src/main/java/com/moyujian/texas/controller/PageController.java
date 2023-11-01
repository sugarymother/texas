package com.moyujian.texas.controller;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.logic.User;
import com.moyujian.texas.response.UserVo;
import com.moyujian.texas.service.UserService;
import com.moyujian.texas.utils.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Value("${fe.websocket.url:ws://localhost:8080/texas/ws}")
    private String wsUrl;

    @Value("${fe.rest.url:http://localhost:8080/texas/}")
    private String restUrl;

    @GetMapping(path = "/")
    public String index() {
        return "redirect:home";
    }

    @GetMapping(path = "/home")
    public String home(@CookieValue(name = Constants.ONLINE_SERIES_COOKIE_NAME, required = false) String onlineSeries,
                       Model model) {
        User user = UserService.getUserBySeries(onlineSeries);
        if (user != null) {
            model.addAttribute(Constants.USER_NAME, UserVo.fromUser(user));
        }
        model.addAttribute(Constants.WS_URL_ATTR_NAME, wsUrl);
        model.addAttribute(Constants.REST_URL_NAME, restUrl);
        return "index";
    }
}
