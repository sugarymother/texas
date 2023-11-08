package com.moyujian.texas.utils;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.logic.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void setCookie(User user, HttpServletRequest request,
                                 HttpServletResponse response, String onlineSeries) {
        if (onlineSeries == null) {
            user.setOnlineSeries(Md5Util.getMd5Hex(IpUtil.getIp(request) + System.currentTimeMillis()));
            Cookie seriesCookie = new Cookie(Constants.ONLINE_SERIES_COOKIE_NAME, user.getOnlineSeries());
            seriesCookie.setHttpOnly(true);
            seriesCookie.setPath("/");
            seriesCookie.setMaxAge(Constants.ONLINE_SERIES_MAX_AGE_IN_SEC);
            response.addCookie(seriesCookie);
        } else {
            user.setOnlineSeries(onlineSeries);
        }
        Cookie tokenCookie = new Cookie(Constants.COOKIE_NAME, user.signToken());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(tokenCookie);
    }
}
