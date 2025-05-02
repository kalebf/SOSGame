package com.example.sosgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HardComputerPlayer extends ComputerPlayer {
    private static final Random random = new Random();
    
    public Move makeMove(GameBoard board) {
        Move winningMove = findWinningMove(board);
        if (winningMove != null) return winningMove;

        Move safeBlockingMove = findSafeBlockingMove(board);
        if (safeBlockingMove != null) return safeBlockingMove;

        Move safeSetupMove = findSafeSetupMove(board);
        if (safeSetupMove != null) return safeSetupMove;

        return makeSafeRandomMove(board);
    }

    private Move findWinningMove(GameBoard board) {
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isCellEmpty(row, col)) {
                    if (wouldCompleteSOS(board, row, col, 'S')) {
                        return new Move(row, col, 'S');
                    }
                    if (wouldCompleteSOS(board, row, col, 'O')) {
                        return new Move(row, col, 'O');
                    }
                }
            }
        }
        return null;
    }

    private Move findSafeBlockingMove(GameBoard board) {
        List<Move> allBlockingMoves = new ArrayList<>();
        
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isCellEmpty(row, col)) {
                    if (wouldCompleteSOS(board, row, col, 'S')) {
                        allBlockingMoves.add(new Move(row, col, 'S'));
                    }
                    if (wouldCompleteSOS(board, row, col, 'O')) {
                        allBlockingMoves.add(new Move(row, col, 'O'));
                    }
                }
            }
        }

        for (Move move : allBlockingMoves) {
            board.placeSymbol(move.row, move.col, move.symbol);
            
            boolean createsThreat = false;
            for (int r = 0; r < board.getSize(); r++) {
                for (int c = 0; c < board.getSize(); c++) {
                    if (board.isCellEmpty(r, c)) {
                        if (wouldCompleteSOS(board, r, c, 'S') || 
                            wouldCompleteSOS(board, r, c, 'O')) {
                            createsThreat = true;
                            break;
                        }
                    }
                }
                if (createsThreat) break;
            }
            
            board.placeSymbol(move.row, move.col, ' ');
            
            if (!createsThreat) {
                return move;
            }
        }
        
        return allBlockingMoves.isEmpty() ? null : allBlockingMoves.get(0);
    }

    private Move findSafeSetupMove(GameBoard board) {
        List<Move> potentialMoves = new ArrayList<>();
        
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isCellEmpty(row, col)) {
                    potentialMoves.add(new Move(row, col, 'S'));
                    potentialMoves.add(new Move(row, col, 'O'));
                }
            }
        }
        
        Collections.shuffle(potentialMoves);
        
        for (Move move : potentialMoves) {
            board.placeSymbol(move.row, move.col, move.symbol);
            
            boolean createsThreat = false;
            for (int r = 0; r < board.getSize(); r++) {
                for (int c = 0; c < board.getSize(); c++) {
                    if (board.isCellEmpty(r, c)) {
                        if (wouldCompleteSOS(board, r, c, 'S') || 
                            wouldCompleteSOS(board, r, c, 'O')) {
                            createsThreat = true;
                            break;
                        }
                    }
                }
                if (createsThreat) break;
            }
            
            board.placeSymbol(move.row, move.col, ' ');
            
            if (!createsThreat) {
                return move;
            }
        }
        
        return null;
    }

    private Move makeSafeRandomMove(GameBoard board) {
        List<Move> safeMoves = new ArrayList<>();
        
        for (int row = 0; row < board.getSize(); row++) {
            for (int col = 0; col < board.getSize(); col++) {
                if (board.isCellEmpty(row, col)) {
                    safeMoves.add(new Move(row, col, 'S'));
                    safeMoves.add(new Move(row, col, 'O'));
                }
            }
        }
        
        Collections.shuffle(safeMoves);
        
        for (Move move : safeMoves) {
            board.placeSymbol(move.row, move.col, move.symbol);
            
            boolean createsThreat = false;
            for (int r = 0; r < board.getSize(); r++) {
                for (int c = 0; c < board.getSize(); c++) {
                    if (board.isCellEmpty(r, c)) {
                        if (wouldCompleteSOS(board, r, c, 'S') || 
                            wouldCompleteSOS(board, r, c, 'O')) {
                            createsThreat = true;
                            break;
                        }
                    }
                }
                if (createsThreat) break;
            }
            
            board.placeSymbol(move.row, move.col, ' ');
            
            if (!createsThreat) {
                return move;
            }
        }
        
        return safeMoves.isEmpty() ? null : safeMoves.get(0);
    }
}