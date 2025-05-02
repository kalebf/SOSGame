package com.example.sosgame;

public class Move {
    public final int row;
    public final int col;
    public final char symbol;
    
    public Move(int row, int col, char symbol) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
    }
}