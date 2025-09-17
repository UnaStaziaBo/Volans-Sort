package com.example.game.dragons.levels;

import com.example.game.dragons.characters.Dragon;
import com.example.game.dragons.rules.RuleComponent;

import java.util.List;

public class GenericLevel extends GameLevel{
    public GenericLevel(int levelNumber, int rowCount, int columnCount, int maxMoves,
                        List<Dragon> dragons, RuleComponent rule) {
        super(levelNumber, rowCount, columnCount, maxMoves, dragons, rule);
    }
}