package com.zivlazarov.newengine.ai;

import com.zivlazarov.newengine.model.ZMove;
import com.zivlazarov.newengine.model.ZobristBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ZMinimax {

    public double search(ZobristBoard board, int depth, double alpha, double beta, boolean isMaximizing) {
        if (depth == 0) {
            return board.evaluateBoard();
        }

        double bestValue;

        // white
        if (isMaximizing) {
            board.setCurrentPlayer(1);
            List<ZMove> moves = board.generateMoves().stream().toList();

            bestValue = Integer.MIN_VALUE;
            for (ZMove move : moves) {
                board.makeMove(move);
                bestValue = Math.max(bestValue, search(board, depth - 1, alpha, beta, false));
                board.unmakeMove(move);
                // pruning
                if (bestValue >= beta) break;
                alpha = Math.max(alpha, bestValue);
            }
        } else {
            board.setCurrentPlayer(-1);
            List<ZMove> moves = board.generateMoves().stream().toList();

            bestValue = Integer.MAX_VALUE;
            for (ZMove move : moves) {
                board.makeMove(move);
                bestValue = Math.min(bestValue, search(board, depth - 1, alpha, beta, true));
                board.unmakeMove(move);
                // pruning
                if (bestValue <= alpha) break;
                beta = Math.min(beta, bestValue);
            }
        }
        return bestValue;
    }

    public ZMove searchBestMove(ZobristBoard board, int depth, int player) {
        ZMove bestMove = null;

        double bestValue = player == 1 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        boolean isMaximizing = player == 1;

        List<ZMove> moves = new ArrayList<>(board.generateMoves().stream().toList());
        Collections.shuffle(moves);

        for (ZMove move : moves) {
            board.makeMove(move);
            double boardValue = -search(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizing);
            board.unmakeMove(move);

            if (isMaximizing) {
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
