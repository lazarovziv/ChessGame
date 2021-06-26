package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.KnightPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class KnightPieceTest {

    private static Board board;
    private static KnightPiece knightPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        knightPiece = new KnightPiece(player, board, PieceColor.WHITE, board.getBoard()[0][1], 0);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[2][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[1][3], 0);
        board.checkBoard(player);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = knightPiece.getTilesToMoveTo();
//        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
//        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[1][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        pawnPiece.moveToTile(board.getBoard()[3][2]);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard(player);

        List<Tile> tilesGenerated = knightPiece.getTilesToMoveTo();
        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][2]);
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[1][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }
}
