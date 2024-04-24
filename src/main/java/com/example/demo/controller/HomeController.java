package com.example.demo.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // Страница приветствия
    }

    @GetMapping("/profile")
    public String profile(Model model, OAuth2AuthenticationToken authentication) {
        var principal = authentication.getPrincipal();
        var attributes = principal.getAttributes();
        model.addAttribute("name", attributes.get("name"));
        model.addAttribute("email", attributes.get("email"));
        return "profile"; // Страница профиля
    }
}
