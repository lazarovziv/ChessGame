package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BoardTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static PawnPiece opponentPawnPiece1;

    @BeforeAll
    public static void setup() {
        board = new Board();
        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[1][4]);
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[2][4], 0);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[3][4], 0);
        opponentPawnPiece1 = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[5][0], 1);
        board.checkBoard();
    }

    @Test
    public void testDistanceBetweenPieces() {
        board.printBoard();
        int distance = board.distanceBetweenPieces(kingPiece, opponentPawnPiece);
        int distance1 = board.distanceBetweenPieces(kingPiece, opponentPawnPiece1);
        System.out.println(distance);
        System.out.println(distance1);
        Assertions.assertEquals(2, distance);
        Assertions.assertEquals(4, distance1);
    }
}
