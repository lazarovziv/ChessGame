package com.zivlazarov.test;

import com.zivlazarov.chessengine.pieces.BishopPiece;
import com.zivlazarov.chessengine.pieces.PawnPiece;
import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.PieceColor;
import com.zivlazarov.chessengine.utils.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class BishopPieceTest {

    private static BishopPiece bishopPiece;
    private static PawnPiece pawnPiece;
    private static Board board;

    @BeforeAll
    public static void setup() {
        board = new Board();
        bishopPiece = new BishopPiece(board, PieceColor.WHITE, board.getBoard()[0][2]);
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[1][3]);
        bishopPiece.init();
        pawnPiece.init();
    }

    @Test
    public void testWhatTilesAreBeingGenerated() {
        List<Tile> tilesGenerated = bishopPiece.getTilesToMoveTo();
//        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
//        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[1][1]);
        tilesTrue.add(board.getBoard()[2][0]);

//        Assertions.assertEquals(tilesGenerated.size(), tilesTrue.size());
        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testIfPieceMovedToTile() {
        Tile tile = board.getBoard()[2][0];
        bishopPiece.moveToTile(tile);

        Assertions.assertEquals(tile, bishopPiece.getCurrentTile());
    }
}
