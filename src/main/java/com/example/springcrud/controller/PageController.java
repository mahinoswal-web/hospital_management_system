package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 1. Handle the Login Page
    @GetMapping("/login")
    public String showLoginPage() {
        // TRY THIS FIRST: Assumes file is at src/main/resources/templates/login.html
        return "login"; 
    }

    // 2. Redirect root URL (localhost:8080) to Login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}