package com.example.game.service;


import com.example.game.entity.User;

public interface UserService {
    void addUser(User user);
    User findUser(String login, String password);
}
