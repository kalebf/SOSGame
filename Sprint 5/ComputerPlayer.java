package com.example.sosgame;

public abstract class ComputerPlayer {
    protected static boolean wouldCompleteSOS(GameBoard board, int row, int col, char symbol) {
        board.placeSymbol(row, col, symbol);
        boolean completesSOS = checkAllDirections(board, row, col, symbol);
        board.placeSymbol(row, col, ' ');
        return completesSOS;
    }

    protected static boolean checkAllDirections(GameBoard board, int row, int col, char symbol) {
        int size = board.getSize();
        char[][] grid = board.getBoard();
        
        if (symbol == 'S') {
            if (col >= 2 && grid[row][col-1] == 'O' && grid[row][col-2] == 'S') return true;
            if (col + 2 < size && grid[row][col+1] == 'O' && grid[row][col+2] == 'S') return true;
            if (row >= 2 && grid[row-1][col] == 'O' && grid[row-2][col] == 'S') return true;
            if (row + 2 < size && grid[row+1][col] == 'O' && grid[row+2][col] == 'S') return true;
            if (row >= 2 && col >= 2 && grid[row-1][col-1] == 'O' && grid[row-2][col-2] == 'S') return true;
            if (row + 2 < size && col + 2 < size && grid[row+1][col+1] == 'O' && grid[row+2][col+2] == 'S') return true;
            if (row >= 2 && col + 2 < size && grid[row-1][col+1] == 'O' && grid[row-2][col+2] == 'S') return true;
            if (row + 2 < size && col >= 2 && grid[row+1][col-1] == 'O' && grid[row+2][col-2] == 'S') return true;
        } else if (symbol == 'O') {
            if (col > 0 && col < size-1 && grid[row][col-1] == 'S' && grid[row][col+1] == 'S') return true;
            if (row > 0 && row < size-1 && grid[row-1][col] == 'S' && grid[row+1][col] == 'S') return true;
            if (row > 0 && col > 0 && row < size-1 && col < size-1 && 
                grid[row-1][col-1] == 'S' && grid[row+1][col+1] == 'S') return true;
            if (row > 0 && col < size-1 && row < size-1 && col > 0 && 
                grid[row-1][col+1] == 'S' && grid[row+1][col-1] == 'S') return true;
        }
        return false;
    }

    public Move makeMove(GameBoard board) {
		// TODO Auto-generated method stub
		return null;
	}
}