package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;

public class DragonD extends Dragon {
    public DragonD() {
        super("Dragon D");
    }

    @Override
    public char[][] getDemoBoard() {
        return new char[][]{
                {' ', 'B', 'C', ' ', ' ', ' ', ' '},
                {' ', 'B', 'D', 'C', ' ', ' ', ' '},
                {'D', 'A', 'A', 'E', 'C', 'E', ' '},
                {'D', 'E', 'A', 'B', 'C', 'E', 'B'},
                {'D', 'A', 'A', 'D', 'C', 'E', 'B'}
        };
    }

    //Delete all D after turn 15 moves
    @Override
    public void specialDragonRule(Field field, int moves) {
        if (moves < 15) return;

        for (int row = 0; row < field.getRowCount(); row++) {
            for (int col = 0; col < field.getColumnCount(); col++) {
                if (field.getField()[row][col] == 'D') {
                    field.getField()[row][col] = ' ';
                }
            }
        }

        System.out.println("\nIt's time for Dragon D to go");
    }
}
