package com.example.game.controller;

import com.example.game.entity.User;
import com.example.game.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
public class UserController {

    @Autowired
    private UserService userService;
    private User loggedUser;


    @RequestMapping("/register")
    public String register(String login, String password, Model model) {
        if(userService.findUser(login, password) == null) {
            userService.addUser(new User(login, password));
            return "register";
        } else {
            model.addAttribute("sorryMessage", "The user " + login + " already exist");
        }
        return "register";
    }

    @RequestMapping("/login")
    public String login(String login, String password) {
        User user = userService.findUser(login, password);
        if(user != null) {
            loggedUser = user;
            return "redirect:/register";
        }
        return "redirect:/register";
    }

    @RequestMapping("/logout")
    public String logout() {
        loggedUser = null;
        return "redirect:/dragonSort";
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean isLogged() {
        return loggedUser != null;
    }
}
