package com.example.game.service;


import com.example.game.entity.Rating;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating) throws RatingException {
        var ratings = entityManager.createQuery("SELECT r FROM Rating r WHERE r.game = :game AND r.player = :player ORDER BY r.ratedOn", Rating.class)
                .setParameter("game", rating.getGame())
                .setParameter("player", rating.getPlayer())
                .getResultStream().findFirst().orElse(null);

        if (ratings != null) {
            ratings.setRating(rating.getRating());
            rating.setRatedOn(ratings.getRatedOn());
            entityManager.merge(ratings);
        } else {
            entityManager.persist(rating);
        }
    }
    @Override
    public int getAverageRating(String game) throws RatingException {
        Double ratings = entityManager.createQuery("select round(AVG(r.rating)) from Rating r where r.game = :game", Double.class)
                .setParameter("game", game)
                .getSingleResult();

        if (ratings == null) {
            return 0;
        }

        return ratings.intValue();
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        var ratings = entityManager.createQuery("select r from Rating r where r.game = :game and r.player = :player order by r.ratedOn", Rating.class)
                .setParameter("game", game)
                .setParameter("player", player)
                .getResultList();

        if (ratings.isEmpty()) {
            throw new RatingException("RatingException in: " + game + " for player: " + player);
        }
        return ratings.get(0).getRating();
    }

    @Override
    public void reset() {
        entityManager.createQuery("DELETE FROM Rating").executeUpdate();
    }
}
