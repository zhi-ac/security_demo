package com.sheng.securitydemo.controller;

import com.sheng.securitydemo.annotation.NoAuthentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "/index";
    }

    @NoAuthentication
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }
}
