package com.example.game.controller;


import com.example.game.dragons.characters.*;
import com.example.game.dragons.core.Field;
import com.example.game.dragons.core.FieldState;
import com.example.game.dragons.levels.GameLevel;
import com.example.game.dragons.levels.GenericLevel;
import com.example.game.dragons.rules.TextRule;
import com.example.game.entity.Comment;
import com.example.game.entity.Rating;
import com.example.game.entity.Score;
import com.example.game.service.CommentService;
import com.example.game.service.RatingService;
import com.example.game.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

@Controller
@RequestMapping("/dragonSort")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class DragonController {

    private final List<GameLevel> levels = new ArrayList<>();
    private final ScoreService scoreService;
    private final CommentService commentService;
    private Field field = new Field(5, 7);
    private final Set<Integer> completedColumnIndexes = new HashSet<>();
    private int submitScore = 0;
    private Integer initialCol = null;
    private int currentLevelIndex = 0;
    public int totalScore = 0;
    private int moves = 0;
    private int score = 0;

    @Autowired
    private UserController userController;

    @Autowired
    private RatingService ratingService;

    public DragonController(ScoreService scoreService, CommentService commentService) {
        levels.add(new GenericLevel(1, 5, 7, 100, Arrays.asList(new DragonA()), new TextRule("rules/level1.txt")));
        levels.add(new GenericLevel(2, 5, 7, 100, Arrays.asList(new DragonB()), new TextRule("rules/level2.txt")));
        levels.add(new GenericLevel(3, 5, 7, 100, Arrays.asList(new DragonC()), new TextRule("rules/level3.txt")));
        levels.add(new GenericLevel(4, 5, 7, 100, Arrays.asList(new DragonD()), new TextRule("rules/level4.txt")));
        levels.add(new GenericLevel(5, 5, 8, 120, Arrays.asList(new DragonE()), new TextRule("rules/level5.txt")));
        this.scoreService = scoreService;
        this.commentService = commentService;
    }

    @RequestMapping
    public String dragonSort(Model model) {

        if(currentLevelIndex >= levels.size()){
            return "redirect:/dragonSort/new";
        }
        GameLevel level = levels.get(currentLevelIndex);
        this.field = level.getField();
        this.initialCol = null;

//        model.addAttribute("fieldHtml", getHtmlField(field, currentLevelIndex));
        model.addAttribute("board", getBoard());
        model.addAttribute("rowCount", getRowCount());
        model.addAttribute("columnCount", getColumnCount());
        model.addAttribute("fieldState", field.getState());
        model.addAttribute("currentLevel", currentLevelIndex);
        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort"));
        model.addAttribute("rule", getRulesText());
        model.addAttribute("maxMoves", level.getMaxMoves());
        model.addAttribute("moves", moves);
        return "dragonSortPage";
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        submitScore = 0;
        currentLevelIndex = 0;
        this.levels.clear();

        for (int levelIndex = 0; levelIndex <= 4; levelIndex++) {
            levels.add(generateLevelByIndex(levelIndex));
        }

        GameLevel level = levels.get(currentLevelIndex);
        this.field = level.getField();

        this.initialCol = null;
        this.moves = 0;
        this.score = 0;
        this.totalScore = 0;
        this.completedColumnIndexes.clear();

//        model.addAttribute("fieldHtml", getHtmlField(field, currentLevelIndex));
        model.addAttribute("board", getBoard());
        model.addAttribute("rowCount", getRowCount());
        model.addAttribute("columnCount", getColumnCount());
        model.addAttribute("currentLevel", currentLevelIndex);
        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort") );
        model.addAttribute("fieldState", field.getState());
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("rule", getRulesText());
        model.addAttribute("maxMoves", level.getMaxMoves());
        model.addAttribute("moves", moves);
        return "dragonSortPage";
    }

    @RequestMapping("/comment")
    public String addComments(@RequestParam String comment) {
        if (userController.isLogged() && submitScore == 0) {
            commentService.addComment(new Comment(userController.getLoggedUser().getLogin(), "dragonSort", comment, new Date()));
            submitScore = 1;
        }
        return "redirect:/dragonSort/finalPage";
    }

    @RequestMapping("/rating")
    public String addRating(@RequestParam int rating) {
        if (userController.isLogged()) {
            ratingService.setRating(new Rating(userController.getLoggedUser().getLogin(), "dragonSort", rating, new Date()));
        }
        return "redirect:/dragonSort/finalPage";
    }

    @RequestMapping("/finalPage")
    public String page(Model model) {

        if(userController.isLogged() && submitScore == 0) {
            scoreService.addScore(new Score(userController.getLoggedUser().getLogin(), "dragonSort", totalScore, new Date()));
        }

        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort") );
        model.addAttribute("totalScore", totalScore);
        return "finalPage";
    }

    @RequestMapping("/skip")
    public String skip(Model model) {
        levels.get(currentLevelIndex).getField().setState(FieldState.SKIPPED);

        currentLevelIndex = currentLevelIndex + 1;

        if (currentLevelIndex >= levels.size()) {
            return "redirect:/dragonSort/finalPage";
        }

        GameLevel level = levels.get(currentLevelIndex);
        this.field = level.getField();
        this.initialCol = null;
        this.moves = 0;
//        model.addAttribute("fieldHtml", getHtmlField(field, currentLevelIndex));
        model.addAttribute("board", getBoard());
        model.addAttribute("rowCount", getRowCount());
        model.addAttribute("columnCount", getColumnCount());
        model.addAttribute("fieldState", field.getState());
        model.addAttribute("currentLevel", currentLevelIndex);
        model.addAttribute("rule", getRulesText());
        model.addAttribute("maxMoves", level.getMaxMoves());
        model.addAttribute("moves", moves);
        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort") );
        model.addAttribute("totalScore", totalScore);

        return "dragonSortPage";
    }

    @RequestMapping("/dragon")
    public String dragonShow(Model model) {
        if (currentLevelIndex >= levels.size()) {
            return "redirect:/dragonSort/finalPage";
        }
        GameLevel level = levels.get(currentLevelIndex);
        this.field = level.getField();
        this.initialCol = null;
        char[][] demoBoard = level.getDragons().get(0).getDemoBoard();
        level.getField().loadDemoBoard(demoBoard);
//        getHtmlField(level.getField(), currentLevelIndex);

//        model.addAttribute("fieldHtml", getHtmlField(field, currentLevelIndex));
        model.addAttribute("board", getBoard());
        model.addAttribute("rowCount", getRowCount());
        model.addAttribute("columnCount", getColumnCount());
        model.addAttribute("fieldState", field.getState());
        model.addAttribute("currentLevel", currentLevelIndex);
        model.addAttribute("maxMoves", level.getMaxMoves());
        model.addAttribute("moves", moves);
        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort"));
        model.addAttribute("totalScore", totalScore);
        return "dragonSortPage";
    }

    @RequestMapping("/level")
    public String playLevel(@RequestParam("col") int col, Model model) {
        if (currentLevelIndex >= levels.size()) {
            return "redirect:/dragonSort/finalPage";
        }
        GameLevel level = levels.get(currentLevelIndex);
        Field field = level.getField();

        boolean moveSuccess = false;


//        getHtmlField(level.getField(), currentLevelIndex);
        int chosenCol = col + 1;
        if (initialCol == null) {
            initialCol = chosenCol;
        } else {
            moveSuccess = level.getField().moveDragon(initialCol, chosenCol);
            initialCol = null;
        }
        this.moves++;
        if (moveSuccess) { //Game process
            String afterMoveState = level.getField().toString();

            level.applyDragonEffects(moves);

            //Change the field if the dragon effect has been used
            if (!afterMoveState.equals(level.getField().toString())) {
                System.out.println("\nDragon activated!");
//                getHtmlField(level.getField(), currentLevelIndex);
            }
//            this.moves++;
            score = 10;

            //bonus score for completed column
            for (int column = 0; column < level.getField().getColumnCount(); column++) {
                if (!completedColumnIndexes.contains(column) && level.getField().isColumnCompleted(column)) {
                    completedColumnIndexes.add(column);
                    totalScore += 100;
                }
            }

            totalScore += score;
            if (moves >= level.getMaxMoves()) {
                level.getField().setState(FieldState.FAILED);
                System.out.println("\nNo more moves! You lost.");
            }

        } else {
            initialCol = chosenCol;
        }

        //Check game states
        if (level.getField().getState() == FieldState.SOLVED) {
            totalScore += 500;
            System.out.println("\nGood news! YOU WON LEVEL!");
            currentLevelIndex++;
            if (currentLevelIndex >= levels.size()) {
                return "redirect:/dragonSort/finalPage";
            }

            level = levels.get(currentLevelIndex);
            this.field = level.getField();

            this.moves = 0;
            completedColumnIndexes.clear();
        } else if (level.getField().getState() == FieldState.FAILED) {
            System.out.println("\nNo more moves! You lost.");
            this.moves = 0;
            completedColumnIndexes.clear();
            return "redirect:/dragonSort/new";
        } else if (level.getField().getState() == FieldState.SKIPPED) {
            System.out.println("\nYou skipped this level.");
            this.moves = 0;
            completedColumnIndexes.clear();
        }

//        model.addAttribute("fieldHtml", getHtmlField(field, currentLevelIndex));
        model.addAttribute("board", getBoard());
        model.addAttribute("rowCount", getRowCount());
        model.addAttribute("columnCount", getColumnCount());
        model.addAttribute("fieldState", field.getState());
        model.addAttribute("currentLevel", currentLevelIndex);
        model.addAttribute("totalScore", totalScore);
        model.addAttribute("moves", moves);
        model.addAttribute("rule", getRulesText());
        model.addAttribute("maxMoves", level.getMaxMoves());
        model.addAttribute("scores", scoreService.getTopScores("dragonSort"));
        model.addAttribute("comments", commentService.getComments("dragonSort"));
        model.addAttribute("rating", ratingService.getAverageRating("dragonSort"));
        return "dragonSortPage";
    }

    private String getRulesText() {
        GameLevel level = levels.get(currentLevelIndex);
        if (level.getRule() instanceof TextRule) {
            return ((TextRule) level.getRule()).getText();
        }
        return "";
    }


    public char[][] getBoard() {
        return field.getField();
    }
    public int getRowCount() {
        return field.getRowCount();
    }
    public int getColumnCount() {
        return field.getColumnCount();
    }

    private GameLevel generateLevelByIndex(int index) {
        switch (index) {
            case 0:
                return new GenericLevel(1, 5, 7, 100, Arrays.asList(new DragonA()), new TextRule("rules/level1.txt"));
            case 1:
                return new GenericLevel(2, 5, 7, 100, Arrays.asList(new DragonB()), new TextRule("rules/level2.txt"));
            case 2:
                return new GenericLevel(3, 5, 7, 100, Arrays.asList(new DragonC()), new TextRule("rules/level3.txt"));
            case 3:
                return new GenericLevel(4, 5, 7, 100, Arrays.asList(new DragonD()), new TextRule("rules/level4.txt"));
            case 4:
                return new GenericLevel(5, 5, 8, 120, Arrays.asList(new DragonE()), new TextRule("rules/level5.txt"));
            default:
                throw new IllegalArgumentException("Invalid level index: " + index);
        }
    }

}
