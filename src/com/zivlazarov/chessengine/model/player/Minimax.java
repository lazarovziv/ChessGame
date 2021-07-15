package com.zivlazarov.chessengine.model.player;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Minimax {

    GameSituation[] checkmateSituations = new GameSituation[]{GameSituation.BLACK_CHECKMATED, GameSituation.WHITE_CHECKMATED};
    List<GameSituation> checkmateSituationsList = new ArrayList<GameSituation>(Arrays.asList(checkmateSituations));

    /*
    taken from wikipedia

    function minimax(node, depth, maximizingPlayer) is
    if depth = 0 or node is a terminal node then
        return the heuristic value of node
    if maximizingPlayer then
        value := −∞
        for each child of node do
            value := max(value, minimax(child, depth − 1, FALSE))
        return value
    else (* minimizing player *)
        value := +∞
        for each child of node do
            value := min(value, minimax(child, depth − 1, TRUE))
        return value
(* Initial call *)
minimax(currentBoardNode, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true)
     */

    // after each generation of moves, make that move,
    // add the board's state after the move as a child node to the current node, undo the move, and set the current node as the current board state

    // adding the minimax algorithm here, and even optioning human players to be advised for best possible move at any given moment of their turn

    public int minimax(Board board, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth == 0 || checkmateSituationsList.contains(board.getGameSituation())) return board.getHeuristicScore();

        board.checkBoard(board.getCurrentPlayer());

        int value;

        if (isMaximizingPlayer) {
            value = Integer.MIN_VALUE;
            ArrayList<Piece> movablePieces = new ArrayList<>(board.getCurrentPlayer().getAlivePieces().stream().filter(Piece::canMove).toList());
//            Collections.shuffle(movablePieces);
            for (Piece piece : movablePieces) {
//                if (!piece.canMove()) continue;
                ArrayList<Tile> possibleMovesList = new ArrayList<>(piece.getPossibleMoves());
//                Collections.shuffle(possibleMovesList);
                for (Tile tile : possibleMovesList) {
                    if (board.getCurrentPlayer().movePiece(piece, tile)) {
                        System.out.println(value);
                        System.out.println(board.getGameHistoryMoves().peek().getFirst().getName() + " -> " + board.getGameHistoryMoves().peek().getSecond());
                        board.printBoard();
                        value = Math.max(value, minimax(board, depth - 1, alpha, beta, false));
                        if (value >= beta) break;
                        alpha = Math.max(alpha, beta);
                    }
                }
            }

        } else {
            value = Integer.MAX_VALUE;
            ArrayList<Piece> movablePieces = new ArrayList<>(board.getCurrentPlayer().getAlivePieces().stream().filter(Piece::canMove).toList());
//            Collections.shuffle(movablePieces);
            for (Piece piece : movablePieces) {
//                if (!piece.canMove()) continue;
                ArrayList<Tile> possibleMovesList = new ArrayList<>(piece.getPossibleMoves());
//                Collections.shuffle(possibleMovesList);
                for (Tile tile : possibleMovesList) {
                    if (board.getCurrentPlayer().movePiece(piece, tile)) {
                        System.out.println(value);
                        System.out.println(board.getGameHistoryMoves().peek().getFirst().getName() + " -> " + board.getGameHistoryMoves().peek().getSecond());
                        board.printBoard();
                        value = Math.min(value, minimax(board, depth - 1, alpha, beta, true));
                        if (value <= alpha) break;
                        beta = Math.min(beta, alpha);
                    }
                }
            }
        }
        return value;
    }

    public static int min(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || checkmateSituationsList.contains(board.getGameSituation()) return board.getHeuristicScore();
        
        int value = Integer.MAX_VALUE;
        
        for (Move move : board.getCurrentPlayer().getMoves()) {
            move.makeMove();
            value = Math.min(value, max(board, depth - 1, alpha, beta);
            if (value <= alpha) break;
            beta = Math.min(alpha, beta);
        }
        return value;
    }
    
    public static int max(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || checkmateSituationsList.contains(board.getGameSituation()) return board.getHeuristicScore();
        
        int value = Integer.MIN_VALUE;
        
        for (Move move : board.getCurrentPlayer) {
            move.makeMove();
            value = Math.max(value, min(board, depth - 1, alpha, beta);
            if (beta >= value) break;
            alpha = Math.max(alpha, beta);
        }
    }
    return value;
}
