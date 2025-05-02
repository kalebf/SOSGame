package com.example.sosgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeneralSOSGameTest {
    @Test
    void testGeneralGameScoring() {
        GeneralSOSGame game = new GeneralSOSGame(3);
        game.makeMove(0, 0, 'S', null); // Blue
        game.makeMove(1, 0, 'O', null); // Red
        game.makeMove(0, 1, 'O', null); // Blue
        game.makeMove(1, 1, 'S', null); // Red
        game.makeMove(0, 2, 'S', null); // Blue scores
        
        assertEquals(1, game.getBlueScore());
        assertEquals(0, game.getRedScore());
        assertFalse(game.isGameOver());
    }

    @Test
    void testGeneralGameWinner() {
        GeneralSOSGame game = new GeneralSOSGame(3);
        // Blue creates 2 SOS sequences
        game.makeMove(0, 0, 'S', null);
        game.makeMove(0, 1, 'O', null);
        game.makeMove(0, 2, 'S', null);
        
        // Red creates 1 SOS sequence
        game.makeMove(1, 0, 'S', null);
        game.makeMove(1, 1, 'O', null);
        game.makeMove(1, 2, 'S', null);
        
        // Fill remaining cells
        game.makeMove(2, 0, 'S', null);
        game.makeMove(2, 1, 'O', null);
        game.makeMove(2, 2, 'S', null);
        
        assertTrue(game.isGameOver());
        assertEquals("Blue", game.getWinner());
    }
}