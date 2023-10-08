package com.moyujian.texas.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping(path = "/")
    public String index() {
        return "redirect:home";
    }

    @GetMapping(path = "/home")
    public String home() {
        return "index";
    }
}
