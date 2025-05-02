package com.example.sosgame;

public class SimpleSOSGame extends SOSGameBase {
    public SimpleSOSGame(int boardSize) {
        super(boardSize);
    }

    @Override
    public boolean makeMove(int row, int col, char symbol, String player) {
        if (isGameOver || !board.isCellEmpty(row, col)) {
            return false;
        }

        board.placeSymbol(row, col, symbol);
        if (recordGame) {
            gameMoves.add(new GameMove(row, col, symbol, player));
        }
        
        boolean sosCreated = checkBasicSOS(row, col, symbol);
        
        if (sosCreated) {
            isGameOver = true;
            winner = player;
        } 
        else if (board.isBoardFull()) {
            isGameOver = true;
            winner = null;
        }

        if (!isGameOver) {
            switchPlayer();
        }
        
        return true;
    }
}
