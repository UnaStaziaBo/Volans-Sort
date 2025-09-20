package com.example.game.service;

import com.example.game.entity.Score;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class ScoreServiceJPATest {
    @Autowired
    private ScoreServiceJPA scoreServiceJPA;

    @BeforeEach
    public void reset() {
        scoreServiceJPA.reset();

        assertEquals(0, scoreServiceJPA.getTopScores("dragonSort").size());
    }

    @Test
    public void addScore() {
        var date = new Date();

        scoreServiceJPA.addScore(new Score("Jaro", "dragonSort", 100, date));

        var scores = scoreServiceJPA.getTopScores("dragonSort");
        assertEquals(1, scores.size());
        assertEquals("dragonSort", scores.get(0).getGame());
        assertEquals("Jaro", scores.get(0).getPlayer());
        assertEquals(100, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());
    }

    @Test
    public void getTopScores() {
        var date = new Date();
        scoreServiceJPA.addScore(new Score("Kiko", "dragonSort", 320, date));
        scoreServiceJPA.addScore(new Score("Miro", "dragonSort", 450, date));
        scoreServiceJPA.addScore(new Score("Reno", "dragonSort", 590, date));
        scoreServiceJPA.addScore(new Score("Kiko", "dragonSort", 300, date));

        var scores = scoreServiceJPA.getTopScores("dragonSort");

        assertEquals(4, scores.size());

        assertEquals("dragonSort", scores.get(0).getGame());
        assertEquals("Reno", scores.get(0).getPlayer());
        assertEquals(590, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals("dragonSort", scores.get(1).getGame());
        assertEquals("Miro", scores.get(1).getPlayer());
        assertEquals(450, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals("dragonSort", scores.get(2).getGame());
        assertEquals("Kiko", scores.get(2).getPlayer());
        assertEquals(320, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());
    }
}
