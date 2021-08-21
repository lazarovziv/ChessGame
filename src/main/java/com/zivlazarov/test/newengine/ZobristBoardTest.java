package com.zivlazarov.test.newengine;

import com.zivlazarov.newengine.model.ZMove;
import com.zivlazarov.newengine.model.ZobristBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

public class ZobristBoardTest {

    @Test
    public void testHashing() {
        ZobristBoard zobristBoard = new ZobristBoard();

        zobristBoard.printBoard();
        System.out.println();

        List<ZMove> moves = zobristBoard.generateMoves();

        Assertions.assertNotEquals(0L, zobristBoard.getZobristHash());
    }

    @Test
    public void testMakeMove() {
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

    @Test
    public void testUnmakeMove() {
        ZobristBoard board = new ZobristBoard();

        board.printBoard();
        System.out.println();

        List<ZMove> whiteMoves = board.generateMoves();
        System.out.println(whiteMoves.size());
        whiteMoves.forEach(System.out::println);

        ZMove move = whiteMoves.stream().filter(zMove -> zMove.getTargetRow() == 4 && zMove.getTargetCol() == 4).toList().get(0);
        System.out.println(board.makeMove(move));
        board.printBoard();

        List<ZMove> blackMoves = board.generateMoves();

        System.out.println(board.unmakeMove(move));
        board.printBoard();
    }

    @Test
    public void testUnmakePawnPromotion() {
        ZobristBoard zobristBoard = new ZobristBoard();

        zobristBoard.printBoard();
        System.out.println();

        List<ZMove> moves = zobristBoard.generateMoves();
        ZMove move = moves.stream().filter(m -> m.getTargetCol() == 6 && m.getTargetRow() == 7).toList().get(0);
        System.out.println(zobristBoard.makeMove(move));
        zobristBoard.printBoard();

        Assertions.assertEquals(zobristBoard.unmakeMove(move), zobristBoard.getZobristHash());
    }

    @Test
    public void testMakeAndUnmakeEnPassant() {
        ZobristBoard zobristBoard = new ZobristBoard();

        List<ZMove> moves = zobristBoard.generateMoves();
        ZMove whiteMove = moves.stream().filter(m -> m.getSourceRow() == 3).toList().get(0);
        System.out.println(zobristBoard.makeMove(whiteMove));
        zobristBoard.printBoard();

        List<ZMove> blackMoves = zobristBoard.generateMoves();
        ZMove blackMove = blackMoves.stream().filter(m -> m.getTargetRow() == 4 && m.getTargetCol() == 6).toList().get(0);
        System.out.println(zobristBoard.makeMove(blackMove));
        zobristBoard.printBoard();

        moves = zobristBoard.generateMoves();

        whiteMove = moves.stream().filter(m -> m.getTargetRow() == 5 && m.getTargetCol() == 6).toList().get(0);
        System.out.println(zobristBoard.makeMove(whiteMove));
        zobristBoard.printBoard();

        System.out.println(zobristBoard.unmakeMove(whiteMove));
        zobristBoard.printBoard();

    }

    @Test
    public void testMakeCastling() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'R', 'N', 'B', 'Q', 'K', '-', '-', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'r', '-', '-', '-', 'k', 'b', 'n', 'r'}});
        board.printBoard();

        List<ZMove> whiteMoves = board.generateMoves();
        ZMove whiteMove = whiteMoves.stream().filter(m -> m.getSourceCol() == 4).toList().get(2);
        System.out.println(board.makeMove(whiteMove));
        board.printBoard();

        List<ZMove> blackMoves = board.generateMoves();
        ZMove blackMove = blackMoves.stream().filter(m -> m.getSourceCol() == 4).toList().get(2);
        System.out.println(board.makeMove(blackMove));
        board.printBoard();
    }

    @Test
    public void testUnmakeKingSideCastling() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'R', 'N', 'B', 'Q', 'K', '-', '-', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'r', '-', '-', '-', 'k', 'b', 'n', 'r'}});
        board.printBoard();

        List<ZMove> whiteMoves = board.generateMoves();
        ZMove whiteMove = whiteMoves.stream().filter(m -> m.getSourceCol() == 4).toList().get(2);
        System.out.println(board.makeMove(whiteMove));
        board.printBoard();
        System.out.println(board.unmakeMove(whiteMove));
        board.printBoard();
    }

    @Test
    public void testUnmakeQueenSideCastling() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'R', 'N', 'B', 'Q', 'K', '-', '-', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'r', '-', '-', '-', 'k', 'b', 'n', 'r'}});
        board.printBoard();

        List<ZMove> whiteMoves = board.generateMoves();
        ZMove whiteMove = whiteMoves.stream().filter(m -> m.getSourceCol() == 4).toList().get(2);
        System.out.println(board.makeMove(whiteMove));
        board.printBoard();

        List<ZMove> blackMoves = board.generateMoves();
        ZMove blackMove = blackMoves.stream().filter(m -> m.getSourceCol() == 4).toList().get(2);
        System.out.println(board.makeMove(blackMove));
        board.printBoard();

        System.out.println(board.unmakeMove(blackMove));
        board.printBoard();
    }

    @Test
    public void testIsInCheck() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'R', 'N', 'B', 'Q', 'K', '-', '-', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', 'B'},
                {'-', '-', '-', '-', '-', '-', 'p', '-'},
                {'p', 'p', 'p', 'p', 'p', '-', '-', 'p'},
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}});
        board.printBoard();

        int whitePlayer = 1;
        int blackPlayer = -1;

        List<ZMove> whiteMoves = board.generateMoves();
        ZMove whiteMove = whiteMoves.stream().filter(m -> m.getTargetRow() == 5 && m.getTargetCol() == 6).toList().get(0);
        board.makeMove(whiteMove);
        board.printBoard();

        if (board.isPlayerInCheck(blackPlayer)) System.out.println("Check");


//        Assertions.assertTrue(board.isPlayerInCheck(blackPlayer));
//        Assertions.assertFalse(board.isPlayerInCheck(whitePlayer));
    }

    @Test
    public void testRookMoves() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'-', '-', '-', '-', 'K', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', 'R', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', 'k', '-', '-', '-'}});

        board.generateMoves().forEach(System.out::println);
    }

    @Test
    public void testCapture() {
        ZobristBoard board = new ZobristBoard();
        board.setDisplayBoard(new char[][] {
                {'-', '-', '-', '-', 'K', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', 'R', '-', '-', '-', '-'},
                {'-', '-', '-', 'p', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', 'k', '-', '-', '-'}});
        board.printBoard();

        List<ZMove> whiteMoves = board.generateMoves();
        ZMove whiteMove = whiteMoves.stream().filter(m -> m.getTargetRow() == 4 && m.getTargetCol() == 3).toList().get(0);
        System.out.println(board.makeMove(whiteMove));
        board.printBoard();
        System.out.println(board.unmakeMove(whiteMove));
        board.printBoard();
    }
}
