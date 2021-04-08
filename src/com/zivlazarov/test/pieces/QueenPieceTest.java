package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.pieces.PawnPiece;
import com.zivlazarov.chessengine.pieces.QueenPiece;
import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.PieceColor;
import com.zivlazarov.chessengine.utils.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class QueenPieceTest {

    private static Board board;
    private static QueenPiece queenPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;

    @BeforeAll
    public static void setup() {
        board = new Board();
        queenPiece = new QueenPiece(board, PieceColor.WHITE, board.getBoard()[3][3]);
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[4][3]);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[5][3]);
        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = pawnPiece.getTilesToMoveTo();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();

        int queenRow = queenPiece.getCurrentTile().getRow();
        int queenCol = queenPiece.getCurrentTile().getCol();

//        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }
}
