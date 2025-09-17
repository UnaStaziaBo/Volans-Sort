package com.example.game.dragons.levels;

import com.example.game.dragons.characters.Dragon;
import com.example.game.dragons.core.Field;
import com.example.game.dragons.rules.RuleComponent;

import java.util.List;

public abstract class GameLevel {
    protected Field field;
    protected int levelNumber;
    protected int maxMoves;
    protected List<Dragon> dragons;
    protected
    RuleComponent rule;

    public GameLevel(int levelNumber, int rowCount, int columnCount, int maxMoves, List<Dragon> dragons, RuleComponent rule) {
        this.levelNumber = levelNumber;
        this.maxMoves = maxMoves;
        this.field = new Field(rowCount, columnCount);
        this.dragons = dragons;
        this.rule = rule;
    }

    public Field getField() {
        return field;
    }
    public int getLevelNumber() {
        return levelNumber;
    }
    public int getMaxMoves() {
        return maxMoves;
    }

    public List<Dragon> getDragons() {
        return dragons;
    }

    public void showRule() {
        if (rule != null) {
            rule.showRule();
        }
    }

    // Check if dragons are applied to the level
    public void applyDragonEffects(int moves) {
        if (dragons != null && !dragons.isEmpty()) {
            for (Dragon dragon : dragons) {
                dragon.specialDragonRule(field, moves);
            }
        }
    }

    public RuleComponent getRule() {
        return rule;
    }

}
