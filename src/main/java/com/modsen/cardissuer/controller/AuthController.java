package com.modsen.cardissuer.controller;

import com.modsen.cardissuer.dto.UserDto;
import com.modsen.cardissuer.model.Company;
import com.modsen.cardissuer.model.Role;
import com.modsen.cardissuer.service.CompanyService;
import com.modsen.cardissuer.service.RoleService;
import com.modsen.cardissuer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final RoleService roleService;
    private final CompanyService companyService;

    @Autowired
    public AuthController(UserService userService, RoleService roleService, CompanyService companyService) {
        this.userService = userService;
        this.roleService = roleService;
        this.companyService = companyService;
    }


    @GetMapping("/login")
    public String getLoginPage() {
        return "login";
    }

    @GetMapping("/fail")
    public String loginErrorPage() {
        return "fail";
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        final List<Company> companies = companyService.getAllCompanies();
        final List<Role> roles = roleService.getAllRoles();
        model.addAttribute("companies", companies);
        model.addAttribute("roles", roles);
        model.addAttribute("user", new UserDto());

        return "register";
    }

    @PostMapping("/register")
    public String registerNewUser(@Valid UserDto user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            bindingResult.rejectValue("password", "", "Password don't match");
        }
        userService.save(user);
        return "redirect:/welcome";
    }
}
