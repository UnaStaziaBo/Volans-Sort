package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;

public class DragonE extends Dragon {
    private boolean eCollected = false;

    public DragonE() {
        super("Dragon E");
    }

    @Override
    public char[][] getDemoBoard() {
        return new char[][]{
                {' ', 'D', ' ', ' ', ' ', ' ', ' ', 'E'},
                {' ', 'C', 'B', ' ', ' ', 'B', 'F', 'E'},
                {'D', 'C', 'D', 'A', ' ', 'B', 'F', 'A'},
                {'D', 'C', 'E', 'A', 'F', 'B', 'F', 'E'},
                {'E', 'C', 'C', 'A', 'D', 'B', 'F', 'A'}
        };
    }

    //Drop all E down to collect F first
    @Override
    public void specialDragonRule(Field field, int moves) {

        if (eCollected) return;

        if (!areAllFInOneColumn(field)) return;

        int emptyCol = findEmptyColumn(field);
        if (emptyCol == -1) {
            System.out.println("All F collected, but no empty column found for E.");
            return;
        }


        int rowCount = field.getRowCount();
        int columnCount = field.getColumnCount();
        int eCount = 0;

        for (int col = 0; col < columnCount; col++) {
            for (int row = 0; row < rowCount; row++) {
                if (field.getField()[row][col] == 'E') {
                    field.getField()[row][col] = ' ';
                    eCount++;
                }
            }
        }


        int row = rowCount - 1;
        while (eCount > 0 && row >= 0) {
            field.getField()[row][emptyCol] = 'E';
            row--;
            eCount--;
        }

        System.out.println("\nDragon F collected all E into column " + (emptyCol + 1) + "!");
        eCollected = true;
    }

    private boolean areAllFInOneColumn(Field field) {
        int columnWithF = -1;

        for (int col = 0; col < field.getColumnCount(); col++) {
            for (int row = 0; row < field.getRowCount(); row++) {
                char cell = field.getField()[row][col];
                if (cell == 'F') {
                    if (columnWithF == -1) {
                        columnWithF = col;
                    } else if (columnWithF != col) {
                        return false;
                    }
                }
            }
        }

        return columnWithF != -1;
    }

    private int findEmptyColumn(Field field) {
        for (int col = 0; col < field.getColumnCount(); col++) {
            boolean isEmpty = true;
            for (int row = 0; row < field.getRowCount(); row++) {
                if (field.getField()[row][col] != ' ') {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) return col;
        }
        return -1;
    }
}