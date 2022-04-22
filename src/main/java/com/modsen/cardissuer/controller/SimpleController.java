package com.modsen.cardissuer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SimpleController {

    @Value("${spring.application.name}")
    String appName;

    @GetMapping(value = {"/", "/home"})
    public String homePage(Model model) {
        model.addAttribute("appName", appName);
        return "home";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('all permission')")
    public String adminPage(Model model) {
        return "admin";
    }

    @GetMapping("/personalAccount")
    public String personalAccountPage() {
        return "/personalAccount";
    }
}
