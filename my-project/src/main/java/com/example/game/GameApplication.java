package com.example.game;

import com.example.game.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EntityScan(basePackages = "com.example.game.entity")
public class GameApplication {
    public static void main(String[] args) {
        SpringApplication.run(GameApplication.class);
    }

    @Bean
    public ScoreService scoreService() {return new ScoreServiceJPA();}

    @Bean
    CommentService commentService() {return new CommentServiceJPA();}

    @Bean
    RatingService ratingService() {return new RatingServiceJPA();}
    @Bean
    UserService userService() {return new UserServiceJPA();}

}
