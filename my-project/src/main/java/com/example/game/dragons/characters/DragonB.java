package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;

public class DragonB extends Dragon {
    private boolean effectUsed = false;

    public DragonB() {
        super("Dragon B");
    }

    @Override
    public void specialDragonRule(Field field, int moves) {
        applyDragonEffect(field);
    }

    @Override
    public char[][] getDemoBoard() {
        return new char[][]{
                {' ', ' ', ' ', ' ', 'A', 'E', ' '},
                {'B', ' ', 'D', ' ', 'C', 'E', ' '},
                {'B', 'C', 'D', ' ', 'E', 'E', 'B'},
                {'B', 'C', 'D', ' ', 'C', 'D', 'A'},
                {'B', 'C', 'D', 'A', 'E', 'A', 'A'}
        };
    }

    //Get all A or you don't get points
    private void applyDragonEffect(Field field) {
        if (effectUsed) {
            return;
        }

        int columnB = -1;
        boolean foundB = false;

        //Is there a column that contains only B and empty cells
        for(int col = 0; col < field.getColumnCount(); col++){
            boolean hasB = false;
            boolean onlyB = true;

            for(int row = 0; row < field.getRowCount(); row++){
                char cell = field.getField()[row][col];

                if(cell == 'B') hasB = true;

                else if(cell != ' '){
                    onlyB = false;
                    break;
                }
            }
            if(hasB && onlyB){
                columnB = col;
                foundB = true;
                break;
            }
        }

        if(!foundB){
            return;
        }

        //Check if there are B's in other columns
        for(int i = 0; i < field.getColumnCount(); i++){
            if(i == columnB) {
                continue;
            }
            for(int y = 0; y < field.getRowCount(); y++){
                if(field.getField()[y][i] == 'B'){
                    return;
                }
            }
        }

        //If B is collected in one column - delete C
        for(int col = 0; col < field.getColumnCount(); col++){
            for(int row = 0; row < field.getRowCount(); row++){
                if(field.getField()[row][col] == 'A'){
                    field.getField()[row][col] = ' ';
                }
            }
        }

        effectUsed = true;
        System.out.println("\nDragon B has done his job, say goodbye to C!");
    }
}
