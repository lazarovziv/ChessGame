package com.zivlazarov.chessengine.model.ai;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;

import java.util.ArrayList;
import java.util.Collections;

public class Minimax {

    public int search(Board board, int depth, int alpha, int beta) {
        if (depth == 0 || board.getGameSituation() == GameSituation.BLACK_CHECKMATED
        || board.getGameSituation() == GameSituation.WHITE_CHECKMATED) return board.evaluateBoard();

        int value;

        if (board.getCurrentPlayer().getPlayerColor() == PieceColor.WHITE) {
            value = Integer.MIN_VALUE;

            Collections.shuffle(board.getCurrentPlayer().getMoves());

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

            Collections.shuffle(board.getCurrentPlayer().getMoves());

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

    public int search1(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || board.getGameSituation() == GameSituation.BLACK_CHECKMATED
                || board.getGameSituation() == GameSituation.WHITE_CHECKMATED) return board.evaluateBoard();

        int value;

        if (isMaximizing) {
            value = Integer.MIN_VALUE;

            for (Piece piece : board.getCurrentPlayer().getAlivePieces()) {
                Collections.shuffle(piece.getPossibleMoves());
                for (Tile tile : new ArrayList<>(piece.getPossibleMoves())) {
                    board.getCurrentPlayer().movePiece(piece, tile);
                    value = Math.max(value, search1(board, depth - 1, alpha, beta,  false));
                    board.getCurrentPlayer().undoLastMove();

                    alpha = Math.max(alpha, value);
                    if (value >= beta) break;
                }
            }
        } else {
            value = Integer.MAX_VALUE;

            for (Piece piece : board.getCurrentPlayer().getAlivePieces()) {
                Collections.shuffle(piece.getPossibleMoves());
                for (Tile tile : new ArrayList<>(piece.getPossibleMoves())) {
                    board.getCurrentPlayer().movePiece(piece, tile);
                    value = Math.min(value, search1(board, depth - 1, alpha, beta,  false));
                    board.getCurrentPlayer().undoLastMove();

                    beta = Math.min(beta, value);
                    if (value <= alpha) break;
                }
            }
        }
        return value;
    }
}
