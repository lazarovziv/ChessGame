package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.pieces.KnightPiece;
import com.zivlazarov.chessengine.pieces.PawnPiece;
import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.PieceColor;
import org.junit.jupiter.api.BeforeAll;

public class PawnPieceTest {

    private static Board board;
    private static PawnPiece pawnPiece;
    private static KnightPiece knightPiece;
    private static PawnPiece opponentPawnPiece;

    @BeforeAll
    public static void setup() {
        board = new Board();
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][0]);
        knightPiece = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[2][0]);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[2][2]);
        board.checkBoard();
    }


}
