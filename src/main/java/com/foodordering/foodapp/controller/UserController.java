package com.foodordering.foodapp.controller;

import com.foodordering.foodapp.model.User;
import com.foodordering.foodapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());

        // Assuming you have an enum inside User like: public enum Role { USER, ADMIN }
        model.addAttribute("roles", Arrays.asList(User.Role.values()));

        return "register"; // Name of your Thymeleaf template: register.html
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        if(userService.existsByUsername(user.getUsername()))
        {
            model.addAttribute("error","Username already present !!");
            return "register";
        }
        if(userService.existsByEmail(user.getEmail()))
        {
            model.addAttribute("error","Email already present !!");
            return "register";
        }
        userService.registerUser(user);
        model.addAttribute("success","Welcome to ");

        return "redirect:/user/home";
    }
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());

        // Assuming you have an enum inside User like: public enum Role { USER, ADMIN }
        model.addAttribute("roles", Arrays.asList(User.Role.values()));

        return "login"; // Name of your Thymeleaf template: register.html
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute("User") User user)
    {

        return "redirect:/user/home";
    }


        @GetMapping("/user/home")
        public String userHome() {
            // Return view name (e.g., userHome.html or user/home.html depending on your setup)
            return "user/home";
        }

}
