package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String get1() {
        return "Admin";
    }

    @GetMapping("/home2")
    public String get2() {
        return "Admin2";
    }

    @GetMapping("/home3")
    public String get3() {
        return "Admin3";
    }

}
