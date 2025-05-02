package com.example.sosgame;

import java.io.Serializable;

public class GameMove implements Serializable {
    private final int row;
    private final int col;
    private final char symbol;
    private final String player;

    public GameMove(int row, int col, char symbol, String player) {
        this.row = row;
        this.col = col;
        this.symbol = symbol;
        this.player = player;
    }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public char getSymbol() { return symbol; }
    public String getPlayer() { return player; }

    @Override
    public String toString() {
        return player + " placed " + symbol + " at (" + row + "," + col + ")";
    }
}
