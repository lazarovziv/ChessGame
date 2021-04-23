package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.pieces.KnightPiece;
import com.zivlazarov.chessengine.pieces.PawnPiece;
import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.PieceColor;
import com.zivlazarov.chessengine.utils.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class PawnPieceTest {

    private static Board board;
    private static PawnPiece pawnPiece;
    private static KnightPiece knightPiece;
    private static PawnPiece opponentPawnPiece;

    @BeforeAll
    public static void setup() {
        board = new Board();
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][0], 0);
        knightPiece = new KnightPiece(board, PieceColor.WHITE, board.getBoard()[3][0], 0);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[2][0], 0);
        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = pawnPiece.getTilesToMoveTo();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(opponentPawnPiece.getCurrentTile());

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        knightPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard();

        List<Tile> tilesGenerated = pawnPiece.getTilesToMoveTo();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[3][0]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }
}
