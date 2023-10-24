package com.moyujian.texas.utils;

import com.moyujian.texas.constants.Constants;
import com.moyujian.texas.logic.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void setCookie(User user, HttpServletResponse response) {
        Cookie seriesCookie = new Cookie(Constants.ONLINE_SERIES_COOKIE_NAME, user.getOnlineSeries());
        seriesCookie.setHttpOnly(true);
        seriesCookie.setPath("/");
        seriesCookie.setMaxAge(Integer.MAX_VALUE);
        Cookie tokenCookie = new Cookie(Constants.COOKIE_NAME, user.signToken());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(seriesCookie);
        response.addCookie(tokenCookie);
    }
}
