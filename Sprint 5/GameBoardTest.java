package com.example.sosgame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    public void setUp() {
        // Initialize a new board before each test
        board = new GameBoard(5); // 5x5 board
    }

    @Test
    public void testInitialization() {
        // Test if the board is initialized correctly
        assertEquals(5, board.getSize(), "Board size should be 5.");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                assertEquals(' ', board.getBoard()[i][j], "All cells should be empty initially.");
            }
        }
    }

    @Test
    public void testPlaceSymbol() {
        // Test placing a symbol on the board
        board.placeSymbol(0, 0, 'S');
        assertEquals('S', board.getBoard()[0][0], "Cell (0, 0) should contain 'S'.");

        board.placeSymbol(1, 1, 'O');
        assertEquals('O', board.getBoard()[1][1], "Cell (1, 1) should contain 'O'.");
    }

    @Test
    public void testIsCellEmpty() {
        // Test if a cell is empty
        assertTrue(board.isCellEmpty(0, 0), "Cell (0, 0) should be empty initially.");
        board.placeSymbol(0, 0, 'S');
        assertFalse(board.isCellEmpty(0, 0), "Cell (0, 0) should not be empty after placing a symbol.");
    }
}