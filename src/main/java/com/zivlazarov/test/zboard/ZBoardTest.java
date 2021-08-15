package com.zivlazarov.test.zboard;

import com.zivlazarov.chessengine.model.board.ZBoard;
import com.zivlazarov.chessengine.model.move.ZMoveGenerator;
import org.junit.jupiter.api.Test;

public class ZBoardTest {

    @Test
    public void testZHash() {
        ZBoard.initZBoard();
        System.out.println(ZBoard.calculateZobristHash());
        System.out.println(ZBoard.calculateZobristHash());
    }

    @Test
    public void testPawnGenerateMoves() {
        ZBoard.initZBoard();
        ZMoveGenerator.generateMoves().forEach(System.out::println);
    }
}
