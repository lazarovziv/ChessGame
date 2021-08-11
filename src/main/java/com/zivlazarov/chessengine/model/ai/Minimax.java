package com.zivlazarov.chessengine.model.ai;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.List;

public class Minimax {

    public Move calculate(Board board, int depth, Player player) {
        Move bestMove = null;

        int value;
        int playerScore = player.getPlayerScore();

        boolean isWhitePlayer = player.getPlayerColor() == PieceColor.WHITE;

        List<Move> playerMoves = player.getMoves().stream().toList();

        if (isWhitePlayer) {
            value = Integer.MIN_VALUE;

            for (Move move : playerMoves) {
                move.makeMove(true);

                value = Math.max(value, search(board, depth - 1, player.getOpponentPlayer()));

                if (value >= playerScore) bestMove = move;

                move.unmakeMove(true);
            }
        } else {
            value = Integer.MAX_VALUE;

            for (Move move : playerMoves) {
                move.makeMove(true);

                value = Math.min(value, search(board, depth - 1, player.getOpponentPlayer()));

                if (value <= playerScore) bestMove = move;

                move.unmakeMove(true);
            }
        }

        return bestMove;
    }

    public int search(Board board, int depth, Player player) {
        if (depth == 0 || board.isCheckmate() || !player.getKing().isAlive()) {
            System.out.println(board.evaluateBoard());
            return board.evaluateBoard();
        }

        int value;

        boolean isWhitePlayer = player.getPlayerColor() == PieceColor.WHITE;

        List<Move> playerMoves = player.getMoves().stream().toList();

        if (isWhitePlayer) {
            value = Integer.MIN_VALUE;
            for (Move move : playerMoves) {
                move.makeMove(true);

                value = Math.max(value, search(board, depth - 1, player.getOpponentPlayer()));

                move.unmakeMove(true);
            }
        } else {
            value = Integer.MAX_VALUE;
            for (Move move : playerMoves) {
                move.makeMove(true);

                value = Math.max(value, search(board, depth - 1, player.getOpponentPlayer()));

                move.unmakeMove(true);
            }
        }

        return value;
    }
}
