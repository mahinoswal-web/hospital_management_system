package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminPageController {

    @GetMapping("/admin/dashboard")
    public String showAdminDashboard(HttpSession session) {
        // Security check: ensure admin is logged in
        if (session.getAttribute("loggedInAdminId") == null) {
            return "redirect:/login";
        }
        // This maps to src/main/resources/templates/admin/dashboard.html
        return "admin/dashboard"; 
    }

    @GetMapping("/admin/doctors")
    public String showDoctorsPage() {
        return "admin/doctors";
    }

    @GetMapping("/admin/patients")
    public String showPatientsPage() {
        return "admin/patients";
    }
}