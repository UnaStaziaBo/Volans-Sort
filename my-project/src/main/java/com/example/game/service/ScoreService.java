package com.example.game.service;

import com.example.game.entity.Score;

import java.util.List;

public interface ScoreService {
    void addScore(Score score);

    List<Score> getTopScores(String game);

    void reset();

}
