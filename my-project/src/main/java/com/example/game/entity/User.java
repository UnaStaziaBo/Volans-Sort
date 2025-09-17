package com.example.game.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "\"user\"")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ident;

    @Column(unique = true)
    private String login;

    private String password;

    public User() { }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public int getIdent() { return ident; }
    public void setIdent(int ident) { this.ident = ident; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
