package com.example.game.service;

import com.example.game.entity.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class CommentServiceJPATest {

    @Autowired
    private CommentServiceJPA commentServiceJPA;

    @BeforeEach
    public void reset() {
        commentServiceJPA.reset();
        assertEquals(0, commentServiceJPA.getComments("dragonSort").size());
    }

    @Test
    public void addComment() {
        commentServiceJPA.reset();
        var comment = new Comment("Miko", "dragonSort", "Super!", new Date());
        commentServiceJPA.addComment(comment);

        List<Comment> comments = commentServiceJPA.getComments("dragonSort");
        assertEquals(1, comments.size());
        assertEquals("Miko", comments.get(0).getPlayer());
        assertEquals("dragonSort", comments.get(0).getGame());
        assertEquals("Super!", comments.get(0).getComment());
    }

    @Test
    public void getComments() {
        commentServiceJPA.reset();
        commentServiceJPA.addComment(new Comment("Miko", "dragonSort", "Super!", new Date()));
        commentServiceJPA.addComment(new Comment("Kiko", "dragonSort", "Woooow", new Date()));
        commentServiceJPA.addComment(new Comment("Miro", "dragonSort", "Nice", new Date()));

        List<Comment> comments = commentServiceJPA.getComments("dragonSort");
        assertEquals(3, comments.size());
    }

}