package com.example.game.dragons.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {
    private Field field;

    @BeforeEach
    public void setUp() {
        int rowCount = 5;
        int columnCount = 7;
        field = new Field(rowCount, columnCount);
    }

    @Test
    public void tesTwoEmptyColumns() {
        int emptyColumnsCount = 0;

        for (int col = 0; col < field.getColumnCount(); col++) {
            boolean empty = true;
            for (int row = 0; row < field.getRowCount(); row++) {
                if (field.getField()[row][col] != ' ') {
                    empty = false;
                    break;
                }
            }
            if (empty) {
                emptyColumnsCount++;
            }
        }
        assertEquals(2, emptyColumnsCount);
    }

    @Test
    public void tesSameColumnsInvalidMove() {
        assertFalse(field.moveDragon(1, 1), "Move dragon to the same column should be invalid");
    }

    @Test
    public void tesInitialSorting() {
        assertEquals(FieldState.PLAYING, field.getState());
    }

    @Test
    public void tesSEtState() {
        field.setState(FieldState.PLAYING);
        assertEquals(FieldState.PLAYING, field.getState());
    }

    @Test
    public void tesFieldSorted() {
        assertFalse(field.isSorted(), "Field sorted should be false right after generation");
    }
}