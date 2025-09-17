package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;

public abstract class Dragon {
    protected String name;

    public Dragon(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void specialDragonRule(Field field, int moves);

    public abstract char[][] getDemoBoard();
}
