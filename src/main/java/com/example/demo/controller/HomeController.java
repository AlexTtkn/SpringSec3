package com.example.demo.controller;

import com.example.demo.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home2")
    public String get() {
        return "Admin2";
    }

    @GetMapping("/home3")
    @PreAuthorize("hasRole('OAUTH2_USER')")
    public String get2() {
        return "Admin3";
    }

}
