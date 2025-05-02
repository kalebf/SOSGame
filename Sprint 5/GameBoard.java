package com.example.sosgame;

public class GameBoard {
    private char[][] board;
    private int size;

    public GameBoard(int size) {
        this.size = size;
        this.board = new char[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = ' ';
            }
        }
    }

    public boolean isCellEmpty(int row, int col) {
        return board[row][col] == ' ';
    }

    public void placeSymbol(int row, int col, char symbol) {
        board[row][col] = symbol;
    }

    public boolean isBoardFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    // Getters
    public char[][] getBoard() { return board; }
    public int getSize() { return size; }
}
