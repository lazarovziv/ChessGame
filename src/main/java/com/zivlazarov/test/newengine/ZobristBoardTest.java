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
        System.out.println(whiteMove);

        zobristBoard.printBoard();

        List<ZMove> blackMoves = zobristBoard.generateMoves();

        ZMove blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));

        System.out.println(zobristBoard.makeMove(blackMove));
        System.out.println(blackMove);

        zobristBoard.printBoard();

        whiteMoves = zobristBoard.generateMoves();

        whiteMove = whiteMoves.get(new Random().nextInt(whiteMoves.size()));
        System.out.println(zobristBoard.makeMove(whiteMove));
        System.out.println(whiteMove);

        zobristBoard.printBoard();

        blackMoves = zobristBoard.generateMoves();

        blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));
        System.out.println(zobristBoard.makeMove(blackMove));
        System.out.println(blackMove);

        zobristBoard.printBoard();

        whiteMoves = zobristBoard.generateMoves();

        whiteMove = whiteMoves.get(new Random().nextInt(whiteMoves.size()));
        System.out.println(zobristBoard.makeMove(whiteMove));
        System.out.println(whiteMove);

        zobristBoard.printBoard();

        blackMoves = zobristBoard.generateMoves();

        blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));
        System.out.println(zobristBoard.makeMove(blackMove));
        System.out.println(blackMove);

        zobristBoard.printBoard();

        whiteMoves = zobristBoard.generateMoves();

        whiteMove = whiteMoves.get(new Random().nextInt(whiteMoves.size()));
        System.out.println(zobristBoard.makeMove(whiteMove));
        System.out.println(whiteMove);

        zobristBoard.printBoard();

        blackMoves = zobristBoard.generateMoves();

        blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));
        System.out.println(zobristBoard.makeMove(blackMove));
        System.out.println(blackMove);

        zobristBoard.printBoard();

        whiteMoves = zobristBoard.generateMoves();

        whiteMove = whiteMoves.get(new Random().nextInt(whiteMoves.size()));
        System.out.println(zobristBoard.makeMove(whiteMove));
        System.out.println(whiteMove);

        zobristBoard.printBoard();

        blackMoves = zobristBoard.generateMoves();

        blackMove = blackMoves.get(new Random().nextInt(blackMoves.size()));
        System.out.println(zobristBoard.makeMove(blackMove));
        System.out.println(blackMove);

        zobristBoard.printBoard();
    }
}
