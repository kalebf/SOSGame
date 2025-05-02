package com.example.sosgame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleSOSGameTest {
    @Test
    void testSimpleGameWin() {
        SimpleSOSGame game = new SimpleSOSGame(3);
        game.makeMove(0, 0, 'S', null); // Blue
        game.makeMove(1, 0, 'O', null); // Red
        game.makeMove(0, 1, 'O', null); // Blue
        game.makeMove(1, 1, 'S', null); // Red
        game.makeMove(0, 2, 'S', null); // Blue should win
        
        assertTrue(game.isGameOver());
        assertEquals("Blue", game.getWinner());
    }

    @Test
    void testSimpleGameDraw() {
        SimpleSOSGame game = new SimpleSOSGame(3);
        
        game.makeMove(0, 0, 'S', null); // Blue
        game.makeMove(0, 1, 'S', null); // Red
        game.makeMove(0, 2, 'S', null); // Blue
        game.makeMove(1, 0, 'S', null); // Red
        game.makeMove(1, 1, 'S', null); // Blue
        game.makeMove(1, 2, 'S', null); // Red
        game.makeMove(2, 0, 'S', null); // Blue
        game.makeMove(2, 1, 'S', null); // Red
        game.makeMove(2, 2, 'S', null); // Blue - final move
        
        assertTrue(game.isGameOver(), "Game should be over when board is full");
        assertNull(game.getWinner(), "There should be no winner in a draw");
    }}