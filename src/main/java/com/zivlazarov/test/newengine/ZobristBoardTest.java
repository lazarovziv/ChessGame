package com.zivlazarov.test.newengine;

import com.zivlazarov.newengine.ZobristBoard;
import com.zivlazarov.newengine.ZMove;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class ZobristBoardTest {

    @Test
    public void testHashing() {
        ZobristBoard zobristBoard = new ZobristBoard();

        zobristBoard.printBoard();
        System.out.println();

        List<ZMove> whiteMoves = zobristBoard.generateMoves();

        ZMove whiteMove = whiteMoves.get(new Random().nextInt(whiteMoves.size()));
        System.out.println(zobristBoard.makeMove(whiteMove));

        zobristBoard.printBoard();

        System.out.println(zobristBoard.unmakeMove(whiteMove));
        zobristBoard.printBoard();

        System.out.println(zobristBoard.makeMove(whiteMove));
        zobristBoard.printBoard();

        List<ZMove> blackMoves = zobristBoard.generateMoves();

        ZMove blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));

        System.out.println(zobristBoard.makeMove(blackMove));
        zobristBoard.printBoard();

        System.out.println(zobristBoard.unmakeMove(blackMove));
        zobristBoard.printBoard();

    }
}
