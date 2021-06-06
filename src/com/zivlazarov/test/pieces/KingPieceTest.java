package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class KingPieceTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;

    @BeforeAll
    public static void setup() {
        board = new Board();
        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[1][4]);
        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[2][4], 0);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[3][4], 0);
        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = kingPiece.getTilesToMoveTo();
        board.printBoard();

        int kingRow = kingPiece.getCurrentTile().getRow();
        int kingCol = kingPiece.getCurrentTile().getCol();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol]);
        tilesTrue.add(board.getBoard()[kingRow][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow][kingCol-1]);
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol-1]);
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol-1]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.moveToTile(board.getBoard()[opponentPawnPiece.getCurrentTile().getRow() - 1][opponentPawnPiece.getCurrentTile().getCol()]);
        board.checkBoard();

        List<Tile> tilesGenerated = kingPiece.getTilesToMoveTo();
        board.printBoard();

        int kingRow = kingPiece.getCurrentTile().getRow();
        int kingCol = kingPiece.getCurrentTile().getCol();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol]);
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol]);
        tilesTrue.add(board.getBoard()[kingRow][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow][kingCol-1]);
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol-1]);
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol+1]);
        tilesTrue.add(board.getBoard()[kingRow-1][kingCol-1]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }
}
