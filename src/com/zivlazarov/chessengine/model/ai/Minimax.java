package com.zivlazarov.chessengine.model.ai;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.GameSituation;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.ArrayList;

public class Minimax {

    private final Player player;

    public Minimax(Player player) {
        this.player = player;
    }

    public int search(Board board, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0 || board.getGameSituation() == GameSituation.BLACK_CHECKMATED
        || board.getGameSituation() == GameSituation.WHITE_CHECKMATED) return board.evaluateBoard();

        int value;

        if (isMaximizing) {
            value = Integer.MIN_VALUE;

            for (Move move : new ArrayList<>(player.getMoves())) {
                move.makeMove();
                value = Math.max(value, search(board, depth - 1, alpha, beta,  false));
                move.unmakeMove();

                alpha = Math.max(alpha, value);
                if (value >= beta) break;
            }
        } else {
            value = Integer.MAX_VALUE;

            for (Move move : new ArrayList<>(player.getMoves())) {
                move.makeMove();
                value = Math.min(value, search(board, depth - 1, alpha, beta, true));
                move.unmakeMove();

                beta = Math.min(beta, value);
                if (value <= alpha) break;
            }
        }
        return value;
    }
}
