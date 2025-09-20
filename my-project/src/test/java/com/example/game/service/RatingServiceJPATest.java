package com.example.game.service;

import com.example.game.entity.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class RatingServiceJPATest {

    @Autowired
    private RatingServiceJPA ratingServiceJPA;

    @BeforeEach
    public void reset() {
        ratingServiceJPA.reset();
        assertEquals(0, ratingServiceJPA.getAverageRating("dragonSort"));
    }

    @Test
    public void setAndGetRating() {
        var date = new Date();
        ratingServiceJPA.setRating(new Rating("Robert", "dragonSort", 4, date));
        assertEquals(4, ratingServiceJPA.getRating("dragonSort", "Robert"));
    }

    @Test
    public void updateRating() {
        var date = new Date();
        ratingServiceJPA.setRating(new Rating("Johanna", "dragonSort", 3, date));
        ratingServiceJPA.setRating(new Rating("Johanna", "dragonSort", 5, date));
        assertEquals(5, ratingServiceJPA.getRating("dragonSort", "Johanna"));
    }

    @Test
    public void getAverageRating() {
        ratingServiceJPA.setRating(new Rating("Robert", "dragonSort", 4, new Date()));
        ratingServiceJPA.setRating(new Rating("Johanna", "dragonSort", 5, new Date()));
        ratingServiceJPA.setRating(new Rating("Karl", "dragonSort", 1, new Date()));

        int average = ratingServiceJPA.getAverageRating("dragonSort");
        assertEquals(3, average);
    }

    @Test
    public void testEmptyAverageRating() {
        int average = ratingServiceJPA.getAverageRating("dragonSort");
        assertEquals(0, average);
    }

}
