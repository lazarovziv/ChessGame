package com.zivlazarov.chessengine.client.model.ai;

import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.GameSituation;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Minimax {

    private final Board board;

    public Minimax(Board board) {
        this.board = board;
    }

    public Move execute(int depth, boolean isMaximizing) {
        if (depth == 0) return null;

        Move bestMove = null;
        int score = board.evaluateBoard();

        Player currentPlayer = board.getCurrentPlayer();

        // white player is maximizing, black minimizing
        if (isMaximizing) {
            for (Move move : new ArrayList<>(currentPlayer.getMoves())) {
                board.printBoard();
                move.makeMove(true);
                int currentScore = board.evaluateBoard();
                if (currentScore >= score) {
                    score = currentScore;
                    bestMove = move;
                }
                execute(depth - 1, false);
                move.unmakeMove(true);
            }
        } else {
            for (Move move : new ArrayList<>(currentPlayer.getMoves())) {
                board.printBoard();
                move.makeMove(true);
                int currentScore = board.evaluateBoard();
                execute(depth - 1, true);
                if (currentScore <= score) {
                    score = currentScore;
                    bestMove = move;
                }
                move.unmakeMove(true);
            }
        }

        return bestMove;
    }

    public int search(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.getGameSituation() == GameSituation.BLACK_CHECKMATED
        || board.getGameSituation() == GameSituation.WHITE_CHECKMATED) return board.evaluateBoard();

        int value;

        if (board.getCurrentPlayer().getPlayerColor() == PieceColor.WHITE) {
            value = Integer.MIN_VALUE;

            Collections.shuffle((List<?>) board.getCurrentPlayer().getMoves());

            for (Move move : new ArrayList<>(board.getCurrentPlayer().getMoves())) {
                System.out.println(board.getCurrentPlayer().getPlayerColor() + ": ");
                System.out.println(move);
                move.makeMove(true);
                System.out.println(board.evaluateBoard());
                System.out.println(board.getGameSituation());
                System.out.println();
                board.printBoard();
                value = Math.max(value, search(board, depth - 1, alpha, beta));
                move.unmakeMove(true);

                alpha = Math.max(alpha, value);
                if (value >= beta) break;
            }
        } else {
//            player = player.getOpponentPlayer();
            value = Integer.MAX_VALUE;

            Collections.shuffle((List<?>) board.getCurrentPlayer().getMoves());

            for (Move move : new ArrayList<>(board.getCurrentPlayer().getMoves())) {
                System.out.println(board.getCurrentPlayer().getPlayerColor() + ": ");
                System.out.println(move);
                move.makeMove(true);
                System.out.println(board.evaluateBoard());
                System.out.println(board.getGameSituation());
                System.out.println();
                board.printBoard();
                value = Math.min(value, search(board, depth - 1, alpha, beta));
                move.unmakeMove(true);

                beta = Math.min(beta, value);
                if (value <= alpha) break;
            }
        }
        return value;
    }
}
