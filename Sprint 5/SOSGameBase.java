package com.example.sosgame;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SOSGameBase {
    protected GameBoard board;
    protected Player currentPlayer;
    protected Player bluePlayer;
    protected Player redPlayer;
    protected boolean isGameOver;
    protected String winner;
    protected boolean recordGame = false;
    protected List<GameMove> gameMoves = new ArrayList<>();
    protected String recordingFileName = "sos_game_recording.txt";

    
    public enum GameMode { SIMPLE, GENERAL }
    
    public void setRecordGame(boolean record) {
        this.recordGame = record;
        if (record) {
            gameMoves.clear();
        }
    }

    public void saveGameRecording() {
        if (!recordGame || gameMoves.isEmpty()) return;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(recordingFileName))) {
            oos.writeObject(gameMoves);
        } catch (IOException e) {
            System.err.println("Failed to save game recording: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<GameMove> loadGameRecording() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(recordingFileName))) {
            return (List<GameMove>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game recording: " + e.getMessage());
            return null;
        }
    }
    
    public SOSGameBase(int boardSize) {
        this.board = new GameBoard(boardSize);
        this.bluePlayer = new Player("Blue", PlayerType.HUMAN);  // Added PlayerType
        this.redPlayer = new Player("Red", PlayerType.HUMAN);    // Added PlayerType
        this.currentPlayer = bluePlayer;
        this.isGameOver = false;
        this.winner = null;
        
    }
    public void resetGame(int newSize) {
        this.board = new GameBoard(newSize);
        this.currentPlayer = bluePlayer;
        this.isGameOver = false;
        this.winner = null;
        
        // Reset any game-specific state
        if (this instanceof GeneralSOSGame) {
            ((GeneralSOSGame) this).blueScore = 0;
            ((GeneralSOSGame) this).redScore = 0;
            ((GeneralSOSGame) this).allSosLines.clear();
        }
    }
    
    public static SOSGameBase createGame(GameMode mode, int size) {
        return mode == GameMode.SIMPLE ? new SimpleSOSGame(size) : new GeneralSOSGame(size);
    }
    
    public abstract boolean makeMove(int row, int col, char symbol, String player);
    
    protected void switchPlayer() {
        currentPlayer = (currentPlayer == bluePlayer) ? redPlayer : bluePlayer;
    }
    
    protected boolean checkBasicSOS(int row, int col, char symbol) {
        if (symbol == 'S') {
            return checkRightForOS(row, col) || checkLeftForOS(row, col) ||
                   checkDownForOS(row, col) || checkUpForOS(row, col) ||
                   checkDiagonalDownRightForOS(row, col) || checkDiagonalUpLeftForOS(row, col) ||
                   checkDiagonalDownLeftForOS(row, col) || checkDiagonalUpRightForOS(row, col);
        } else if (symbol == 'O') {
            return checkLeftAndRightForS(row, col) || checkUpAndDownForS(row, col) ||
                   checkDiagonalsForS(row, col);
        }
        return false;
    }
    
    private boolean checkRightForOS(int row, int col) {
        if (col + 2 >= board.getSize()) return false;
        return board.getBoard()[row][col + 1] == 'O' && 
               board.getBoard()[row][col + 2] == 'S';
    }

    private boolean checkLeftForOS(int row, int col) {
        if (col - 2 < 0) return false;
        return board.getBoard()[row][col - 1] == 'O' && 
               board.getBoard()[row][col - 2] == 'S';
    }

    private boolean checkDownForOS(int row, int col) {
        if (row + 2 >= board.getSize()) return false;
        return board.getBoard()[row + 1][col] == 'O' && 
               board.getBoard()[row + 2][col] == 'S';
    }

    private boolean checkUpForOS(int row, int col) {
        if (row - 2 < 0) return false;
        return board.getBoard()[row - 1][col] == 'O' && 
               board.getBoard()[row - 2][col] == 'S';
    }

    private boolean checkDiagonalDownRightForOS(int row, int col) {
        if (row + 2 >= board.getSize() || col + 2 >= board.getSize()) return false;
        return board.getBoard()[row + 1][col + 1] == 'O' && 
               board.getBoard()[row + 2][col + 2] == 'S';
    }

    private boolean checkDiagonalUpLeftForOS(int row, int col) {
        if (row - 2 < 0 || col - 2 < 0) return false;
        return board.getBoard()[row - 1][col - 1] == 'O' && 
               board.getBoard()[row - 2][col - 2] == 'S';
    }

    private boolean checkDiagonalDownLeftForOS(int row, int col) {
        if (row + 2 >= board.getSize() || col - 2 < 0) return false;
        return board.getBoard()[row + 1][col - 1] == 'O' && 
               board.getBoard()[row + 2][col - 2] == 'S';
    }

    private boolean checkDiagonalUpRightForOS(int row, int col) {
        if (row - 2 < 0 || col + 2 >= board.getSize()) return false;
        return board.getBoard()[row - 1][col + 1] == 'O' && 
               board.getBoard()[row - 2][col + 2] == 'S';
    }

    private boolean checkLeftAndRightForS(int row, int col) {
        if (col - 1 < 0 || col + 1 >= board.getSize()) return false;
        return board.getBoard()[row][col - 1] == 'S' && 
               board.getBoard()[row][col + 1] == 'S';
    }

    private boolean checkUpAndDownForS(int row, int col) {
        if (row - 1 < 0 || row + 1 >= board.getSize()) return false;
        return board.getBoard()[row - 1][col] == 'S' && 
               board.getBoard()[row + 1][col] == 'S';
    }

    private boolean checkDiagonalsForS(int row, int col) {
        boolean diagonal1 = false;
        boolean diagonal2 = false;
        
        if (row - 1 >= 0 && col - 1 >= 0 && 
            row + 1 < board.getSize() && col + 1 < board.getSize()) {
            diagonal1 = board.getBoard()[row - 1][col - 1] == 'S' && 
                        board.getBoard()[row + 1][col + 1] == 'S';
        }
        
        if (row - 1 >= 0 && col + 1 < board.getSize() && 
            row + 1 < board.getSize() && col - 1 >= 0) {
            diagonal2 = board.getBoard()[row - 1][col + 1] == 'S' && 
                        board.getBoard()[row + 1][col - 1] == 'S';
        }
        
        return diagonal1 || diagonal2;
    }
    //Choose player type for each color
    public void setPlayerType(String playerName, PlayerType type) {
        if (bluePlayer.getName().equals(playerName)) {
            bluePlayer = new Player(playerName, type);
        } else if (redPlayer.getName().equals(playerName)) {
            redPlayer = new Player(playerName, type);
        }
        
        // If current player was changed, update reference
        if (currentPlayer.getName().equals(playerName)) {
            currentPlayer = type == bluePlayer.getType() ? bluePlayer : redPlayer;
        }
    }
    //calls to move algorithm 
    public void makeComputerMove() {
        if (currentPlayer.isComputer()) {
            ComputerPlayer computerPlayer;
            if (currentPlayer.getType() == PlayerType.COMPUTER_EASY) {
                computerPlayer = new HardComputerPlayer(); // Default to hard for now
            } else {
                computerPlayer = new HardComputerPlayer();
            }
            
            Move move = computerPlayer.makeMove(board);
            if (move != null) {
                makeMove(move.row, move.col, move.symbol, currentPlayer.getName());
            }
        }
    }

    public boolean isGameOver() { return isGameOver; }
    public String getWinner() { return winner; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameBoard getBoard() { return board; }
}