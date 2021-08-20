package com.zivlazarov.test.newengine;

import com.zivlazarov.newengine.model.ZobristBoard;
import com.zivlazarov.newengine.ai.ZMinimax;
import org.junit.jupiter.api.Test;

public class ZMinimaxTest {

    @Test
    public void testSearch() {
        ZobristBoard board = new ZobristBoard();
        board.printBoard();

        ZMinimax minimax = new ZMinimax();
        System.out.println(minimax.search(board, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, true));
    }

    @Test
    public void testSearchBestMove() {
        ZobristBoard board = new ZobristBoard();
        board.printBoard();

        ZMinimax minimax = new ZMinimax();
        long start = System.nanoTime();
        System.out.println(minimax.searchBestMove(board, 5, 1));
        System.out.println((double) (System.nanoTime() - start) / 1_000_000_000);

        // depth = 7 -> 67.4 seconds
        // depth = 6 -> 5.4 seconds
        // depth = 5 -> 0.56 seconds -> with board.printBoard() calls -> 147.9 seconds (264.28 more times)
    }
}
