package com.psh.bookflow.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String getIndex() {
        return "forward:/frontend/pages/index.html";
    }

    @GetMapping("/accommodation")
    public String getAccommodation() {
        return "forward:/frontend/pages/accommodation.html";
    }

    @GetMapping("/account")
    public String getAccount() {
        return "forward:/frontend/pages/account.html";
    }
}
