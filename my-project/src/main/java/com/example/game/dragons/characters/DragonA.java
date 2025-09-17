package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DragonA extends Dragon {
    private boolean isShuffledOnce = false;

    public DragonA() {
        super("Dragon A");
    }

    //Shuffle all dragons except A in a single column
    @Override
    public void specialDragonRule(Field field, int moves) {
        applyDragonEffect(field);
    }

    @Override
    public char[][] getDemoBoard() {
        return new char[][]{
                {' ', 'B', ' ', ' ', 'D', 'C', ' '},
                {' ', 'B', 'A', ' ', 'D', 'C', 'E'},
                {' ', 'B', 'A', ' ', 'D', 'C', 'E'},
                {' ', 'B', 'A', ' ', 'D', 'C', 'E'},
                {'A', 'B', 'A', 'C', 'D', 'E', 'E'}
        };
    }

    private void applyDragonEffect(Field field) {

        if (isShuffledOnce) return;

        int foundColumn = -1;
        List<Character> otherLetters = new ArrayList<>();

        for (int col = 0; col < field.getColumnCount(); col++) {
            for (int row = 0; row < field.getRowCount(); row++) {
                if (field.getField()[row][col] == 'A') {
                    if (foundColumn == -1) {
                        foundColumn = col;
                    } else if (foundColumn != col) {
                        return;
                    }
                } else if (field.getField()[row][col] != ' ') {
                    otherLetters.add(field.getField()[row][col]);
                }
            }
        }

        if (foundColumn != -1) {
            Collections.shuffle(otherLetters);
            int index = 0;

            for (int col = 0; col < field.getColumnCount(); col++) {
                if (col != foundColumn) {
                    for (int i = 0; i < field.getRowCount(); i++) {
                        if (index < otherLetters.size()) {
                            field.getField()[i][col] = otherLetters.get(index++);
                        } else {
                            field.getField()[i][col] = ' ';
                        }
                    }
                }
            }

            System.out.println("\n" +getName() + " : Letters shuffled!");
            isShuffledOnce = true;
        }
    }
}
