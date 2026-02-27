package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // 1. Handle the Login Page
   @GetMapping("/login")
    public String showLoginPage() {
        // Just "login" without any folder names!
        return "login"; 
    }
    // 2. Redirect root URL to Login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}