package com.example.game.service;

import com.example.game.entity.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CommentServiceJPA implements CommentService{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        entityManager.persist(comment);
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        var comments = entityManager.createQuery("select c from Comment c where c.game = :game order by c.commentedOn desc", Comment.class)
                .setParameter("game", game)
                .setMaxResults(10)
                .getResultList();
        return comments;
    }

    @Override
    public void reset() {
        entityManager.createNativeQuery("DELETE FROM comment").executeUpdate();
    }
}
