package com.example.game.dragons.characters;

import com.example.game.dragons.core.Field;
import com.example.game.dragons.core.FieldState;

public class DragonC extends Dragon {

    public DragonC() {
        super("Dragon C");
    }

    @Override
    public void specialDragonRule(Field field, int moves) {
        applyDragonEffect(field);
    }

    @Override
    public char[][] getDemoBoard() {
        return new char[][]{
                {'B', ' ', ' ', ' ', 'D', ' ', ' '},
                {'B', ' ', ' ', 'A', 'D', 'C', 'E'},
                {'A', 'C', ' ', 'A', 'B', 'C', 'E'},
                {'E', 'B', ' ', 'D', 'A', 'C', 'E'},
                {'D', 'B', ' ', 'D', 'A', 'C', 'E'}
        };
    }

    //Delete all letters except C after C in one column
    private void applyDragonEffect(Field field) {
        int columnC = -1;
        boolean foundC = false;

        //Is there a column that contains only B and empty cells
        for(int col = 0; col < field.getColumnCount(); col++){
            boolean hasC = false;
            boolean onlyC = true;

            for(int row = 0; row < field.getRowCount(); row++){
                char cell = field.getField()[row][col];

                if(cell == 'C') hasC = true;

                else if(cell != ' '){
                    onlyC = false;
                    break;
                }
            }
            if(hasC && onlyC){
                columnC = col;
                foundC = true;
                break;
            }
        }

        if(!foundC){
            return;
        }

        //Check if there are B's in other columns
        for(int i = 0; i < field.getColumnCount(); i++){
            if(i == columnC) {
                continue;
            }
            for(int y = 0; y < field.getRowCount(); y++){
                if(field.getField()[y][i] == 'C'){
                    return;
                }
            }
        }

        for(int i = 0; i < field.getRowCount(); i++){
            for(int y = 0; y < field.getColumnCount(); y++){
                if(field.getField()[i][y] != 'C'){
                    field.getField()[i][y] = ' ';

                }
            }
        }
        System.out.println("\nWhy did you collect dragon C now?");
        field.setState(FieldState.SOLVED);
    }
}
