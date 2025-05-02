package com.example.sosgame;

import java.util.ArrayList;
import java.util.List;

public class GeneralSOSGame extends SOSGameBase {
    int blueScore;
    int redScore;
    List<SOSLine> allSosLines;
    
    public GeneralSOSGame(int boardSize) {
        super(boardSize);
        this.blueScore = 0;
        this.redScore = 0;
        this.allSosLines = new ArrayList<>();
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
        
        List<SOSLine> newSosLines = checkForNewSOS(row, col, symbol);
        
        if (!newSosLines.isEmpty()) {
            if (player.equals("Blue")) {
                blueScore += newSosLines.size();
            } else {
                redScore += newSosLines.size();
            }
            allSosLines.addAll(newSosLines);
            
            if (!board.isBoardFull()) {
                return true;
            }
        }
        
        switchPlayer();
        
        if (board.isBoardFull()) {
            isGameOver = true;
            determineWinner();
        }
        
        return true;
    }

    private List<SOSLine> checkForNewSOS(int row, int col, char symbol) {
        List<SOSLine> newLines = new ArrayList<>();
        checkHorizontalSOS(row, col, symbol, newLines);
        checkVerticalSOS(row, col, symbol, newLines);
        checkDiagonalSOS(row, col, symbol, newLines);
        return newLines;
    }
    
    private void checkHorizontalSOS(int row, int col, char symbol, List<SOSLine> lines) {
        if (col >= 2 && symbol == 'S' && 
            board.getBoard()[row][col-1] == 'O' && 
            board.getBoard()[row][col-2] == 'S') {
            lines.add(new SOSLine(row, col-2, row, col, currentPlayer.getName()));
        }
        
        if (col + 2 < board.getSize() && symbol == 'S' && 
            board.getBoard()[row][col+1] == 'O' && 
            board.getBoard()[row][col+2] == 'S') {
            lines.add(new SOSLine(row, col, row, col+2, currentPlayer.getName()));
        }
        
        if (col > 0 && col + 1 < board.getSize() && symbol == 'O' && 
            board.getBoard()[row][col-1] == 'S' && 
            board.getBoard()[row][col+1] == 'S') {
            lines.add(new SOSLine(row, col-1, row, col+1, currentPlayer.getName()));
        }
    }
    
    private void checkVerticalSOS(int row, int col, char symbol, List<SOSLine> lines) {
        if (row >= 2 && symbol == 'S' && 
            board.getBoard()[row-1][col] == 'O' && 
            board.getBoard()[row-2][col] == 'S') {
            lines.add(new SOSLine(row-2, col, row, col, currentPlayer.getName()));
        }
        
        if (row + 2 < board.getSize() && symbol == 'S' && 
            board.getBoard()[row+1][col] == 'O' && 
            board.getBoard()[row+2][col] == 'S') {
            lines.add(new SOSLine(row, col, row+2, col, currentPlayer.getName()));
        }
        
        if (row > 0 && row + 1 < board.getSize() && symbol == 'O' && 
            board.getBoard()[row-1][col] == 'S' && 
            board.getBoard()[row+1][col] == 'S') {
            lines.add(new SOSLine(row-1, col, row+1, col, currentPlayer.getName()));
        }
    }
    
    private void checkDiagonalSOS(int row, int col, char symbol, List<SOSLine> lines) {
        if (row >= 2 && col >= 2 && symbol == 'S' && 
            board.getBoard()[row-1][col-1] == 'O' && 
            board.getBoard()[row-2][col-2] == 'S') {
            lines.add(new SOSLine(row-2, col-2, row, col, currentPlayer.getName()));
        }
        
        if (row + 2 < board.getSize() && col + 2 < board.getSize() && symbol == 'S' && 
            board.getBoard()[row+1][col+1] == 'O' && 
            board.getBoard()[row+2][col+2] == 'S') {
            lines.add(new SOSLine(row, col, row+2, col+2, currentPlayer.getName()));
        }
        
        if (row > 0 && row + 1 < board.getSize() && 
            col > 0 && col + 1 < board.getSize() && symbol == 'O' && 
            board.getBoard()[row-1][col-1] == 'S' && 
            board.getBoard()[row+1][col+1] == 'S') {
            lines.add(new SOSLine(row-1, col-1, row+1, col+1, currentPlayer.getName()));
        }
        
        if (row >= 2 && col + 2 < board.getSize() && symbol == 'S' && 
            board.getBoard()[row-1][col+1] == 'O' && 
            board.getBoard()[row-2][col+2] == 'S') {
            lines.add(new SOSLine(row-2, col+2, row, col, currentPlayer.getName()));
        }
        
        if (row + 2 < board.getSize() && col >= 2 && symbol == 'S' && 
            board.getBoard()[row+1][col-1] == 'O' && 
            board.getBoard()[row+2][col-2] == 'S') {
            lines.add(new SOSLine(row, col, row+2, col-2, currentPlayer.getName()));
        }
        
        if (row > 0 && row + 1 < board.getSize() && 
            col > 0 && col + 1 < board.getSize() && symbol == 'O' && 
            board.getBoard()[row-1][col+1] == 'S' && 
            board.getBoard()[row+1][col-1] == 'S') {
            lines.add(new SOSLine(row-1, col+1, row+1, col-1, currentPlayer.getName()));
        }
    }
    
    private void determineWinner() {
        if (blueScore > redScore) {
            winner = "Blue";
        } else if (redScore > blueScore) {
            winner = "Red";
        }
    }
    
    public int getBlueScore() { return blueScore; }
    public int getRedScore() { return redScore; }
    public List<SOSLine> getAllSosLines() { return allSosLines; }
    
    public static class SOSLine {
        public final int startRow, startCol, endRow, endCol;
        public final String player;
        
        public SOSLine(int startRow, int startCol, int endRow, int endCol, String player) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.endRow = endRow;
            this.endCol = endCol;
            this.player = player;
        }
    }
}
