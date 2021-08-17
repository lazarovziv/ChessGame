package com.zivlazarov.chessengine.model.ai;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.move.MoveGenerator;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PieceType;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.List;

public class Minimax {

    public double search(Board board, int depth,double alpha, double beta, boolean isMaximizing) {
        if (depth == 0 || !board.canContinueGame()) return board.evaluateBoard();

        List<Move> moves = board.getCurrentPlayer().getMoves().stream().toList();

        // white
        double bestValue;
        if (isMaximizing) {
            bestValue = Integer.MIN_VALUE;
            for (Move move : moves) {
//                System.out.println(move);
                move.makeMove(true, false);
//                if (board.getCurrentPlayer().isInCheck()) {
//                    System.out.println(move.getPlayer().getColor() + ": ");
//                    board.printBoard();
//                }
                //
                bestValue = Math.max(bestValue, search(board, depth - 1, alpha, beta, false));
                move.unmakeMove(true);
//                board.updateObservers();
                // pruning
                if (bestValue >= beta) break;
                alpha = Math.max(alpha, bestValue);
            }
            // black
        } else {
            bestValue = Integer.MAX_VALUE;
            for (Move move : moves) {
//                System.out.println(move);
                move.makeMove(true, false);
//                if (board.getCurrentPlayer().isInCheck()) {
//                    System.out.println(move.getPlayer().getColor() + ": ");
//                    board.printBoard();
//                }
                bestValue = Math.min(bestValue, search(board, depth - 1, alpha, beta, true));
                move.unmakeMove(true);
//                board.updateObservers();
                // pruning
                if (bestValue <= alpha) break;
                beta = Math.min(beta, bestValue);
            }
        }
        return bestValue;
    }

    public Move findBestMove(Board board, int depth, Player player) {
        Move bestMove = null;

        List<Move> moves = player.getMoves().stream().toList();

        double bestValue = player.getColor() == PieceColor.WHITE ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : moves) {
            move.makeMove(true, false);
            double boardValue = -search(board, depth - 1, Integer.MIN_VALUE, Integer.MIN_VALUE, player.getColor() == PieceColor.WHITE);
            move.unmakeMove(true);
//            board.updateObservers();

            if (player.getColor() == PieceColor.WHITE) {
                if (boardValue > bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            } else {
                if (boardValue < bestValue) {
                    bestValue = boardValue;
                    bestMove = move;
                }
            }
        }
        return bestMove;
    }
}
