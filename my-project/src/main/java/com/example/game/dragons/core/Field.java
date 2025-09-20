package com.example.game.dragons.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Field {
    private final int rowCount;

    private final int columnCount;

    private final char[][] field;

    private FieldState state = FieldState.PLAYING;


    public Field(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.field = new char[rowCount][columnCount];
        generateField();
    }

    public void generateField(){
        int[] emptyColumns = chooseTwoColumns();
        List<Character> letters = shuffleLetters();
        fillField(letters, emptyColumns);
    }

    public void loadDemoBoard(char[][] demoBoard){
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                this.field[row][col] = demoBoard[row][col];
            }
        }
    }


    public boolean moveDragon(int from, int to) {
        if (!checkValidMove(from, to)) {
            System.out.println("\nInvalid move!");
            return false;
        }

        int fromCol = from - 1;
        int toCol = to - 1;
        int sourceRow = -1;
        char movingDragon = ' ';
        char topInTarget = ' ';

        //Find the top non-empty dragon and delete it
        for (int row = 0; row < rowCount; row++) {
            if (field[row][fromCol] != ' ') {
                movingDragon = field[row][fromCol];
                field[row][fromCol] = ' ';
                sourceRow = row;
                break;
            }
        }

        if (movingDragon == ' ') {
            System.out.println("\nInvalid move! Column is empty.");
            return false;
        }

        //Find new non-empty dragon
        for (int row = 0; row < rowCount; row++) {
            if (field[row][toCol] != ' ') {
                topInTarget = field[row][toCol];
                break;
            }
        }

        //Prevent a letter from being placed on a character of a different type
        if (topInTarget != ' ' && topInTarget != movingDragon) {
            System.out.println("\nInvalid move! You can only place on the same type.");
            field[sourceRow][fromCol] = movingDragon;
            return false;
        }

        //Move the dragon
        for (int row = rowCount - 1; row >= 0; row--) {
            if (field[row][toCol] == ' ') {
                field[row][toCol] = movingDragon;
                System.out.println("\nMoved " + movingDragon + " from " + from + " to " + to);

                if (isSorted()) {
                    state = FieldState.SOLVED;
                }

                return true;
            }
        }

        System.out.println("\nInvalid move! Destination column is full.");
        field[sourceRow][fromCol] = movingDragon;
        return false;
    }



    public boolean isSorted() {
        for (int col = 0; col < columnCount; col++) {
            char firstDragon = field[0][col];
            for (int row = 1; row < rowCount; row++) {
                if (field[row][col] != firstDragon && field[row][col] != ' ') {
                    return false;
                }
            }
        }
        state = FieldState.SOLVED;
        return true;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                sb.append(field[row][col]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private int[] chooseTwoColumns(){
        Random rand = new Random();
        int emptyColumn1 = rand.nextInt(columnCount);
        int emptyColumn2 = rand.nextInt(columnCount);

        while (emptyColumn2 == emptyColumn1) {
            emptyColumn2 = rand.nextInt(columnCount);
        }

        return new int[]{emptyColumn1, emptyColumn2};
    }

    public List<Character> shuffleLetters(){
        List<Character> letters = new ArrayList<>();
        int numberColumns = columnCount - 2;

        for (int col = 0; col < numberColumns; col++) {
            char letter = (char) ('A' + col); //generate the letters A, B...
            for (int row = 0; row < rowCount; row++) {
                letters.add(letter);
            }
        }

        Collections.shuffle(letters);
        return letters;
    }

    private void fillField(List<Character> letters, int[] emptyColumns){
        int index = 0;
        for (int col = 0; col < columnCount; col++) {
            if (col == emptyColumns[0] || col == emptyColumns[1]) {
                //Leave two empty columns
                for (int row = 0; row < rowCount; row++) {
                    field[row][col] = ' ';
                }
            } else {
                for (int row = 0; row < rowCount; row++) {
                    field[row][col] = letters.get(index++);
                }
            }
        }
    }

    public boolean isColumnCompleted(int col) {
        char first = field[0][col];
        if (first == ' ') return false;

        for (int row = 1; row < rowCount; row++) {
            if (field[row][col] != first) return false;
        }
        return true;
    }

    private boolean checkValidMove(int from, int to) {
        return !(from == to || from < 1 || to < 1 || from > columnCount || to > columnCount);
    }

    public void setField(char[][] newField) {
        for (int row = 0; row < rowCount; row++) {
            System.arraycopy(newField[row], 0, this.field[row], 0, columnCount);
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public char[][] getField() {
        return field;
    }

    public FieldState getState() {
        return state;
    }
    public void setState(FieldState state) {
        this.state = state;
    }

}
