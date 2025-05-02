package com.example.sosgame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ComputerPlayerTest {
    private GameBoard board;
    private ComputerPlayer computerPlayer;
    
    @BeforeEach
    public void setUp() {
        board = new GameBoard(5);
        computerPlayer = new HardComputerPlayer(); // Create an instance of the computer player
    }

    @Test
    public void makeImprovedMove_CompletesWinningSOS() { 
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(0, 1, 'O');
        // Computer should place 'S' at (0,2)
        Move move = computerPlayer.makeMove(board); // Call on instance
        assertEquals(0, move.row);
        assertEquals(2, move.col);
        assertEquals('S', move.symbol);
    }

    @Test
    public void makeImprovedMove_AvoidsCreatingOpponentOpportunities() {
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(2, 0, 'O');
        
        Move move = computerPlayer.makeMove(board); // Call on instance
        board.placeSymbol(move.row, move.col, move.symbol);
        
        // Verify no immediate win opportunity created for opponent
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                if (board.isCellEmpty(r, c)) {
                    assertFalse(ComputerPlayer.wouldCompleteSOS(board, r, c, 'S') ||
                               ComputerPlayer.wouldCompleteSOS(board, r, c, 'O'));
                }
            }
        }
    }

    @Test
    public void testWouldCompleteSOS_Vertical() {
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(1, 0, 'O');
        assertTrue(ComputerPlayer.wouldCompleteSOS(board, 2, 0, 'S')); // Call on instance
    }

    @Test
    public void testWouldCompleteSOS_Horizontal() {
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(0, 1, 'O');
        assertTrue(ComputerPlayer.wouldCompleteSOS(board, 0, 2, 'S')); // Call on instance
    }

    @Test
    public void testWouldCompleteSOS_Diagonal() {
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(1, 1, 'O');
        assertTrue(ComputerPlayer.wouldCompleteSOS(board, 2, 2, 'S')); // Call on instance
    }

    @Test
    public void testWouldCompleteSOS_OInMiddle() {
        board.placeSymbol(0, 0, 'S');
        board.placeSymbol(0, 2, 'S');
        assertTrue(ComputerPlayer.wouldCompleteSOS(board, 0, 1, 'O')); // Call on instance
    }
}