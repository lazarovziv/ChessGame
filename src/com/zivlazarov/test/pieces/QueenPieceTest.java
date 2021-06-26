package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.QueenPiece;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
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
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        queenPiece = new QueenPiece(player, board, PieceColor.WHITE, board.getBoard()[3][3]);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[4][3], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[5][3], 0);
        board.checkBoard(player);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = queenPiece.getTilesToMoveTo();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();

        tilesTrue.add(board.getBoard()[4][4]);
        tilesTrue.add(board.getBoard()[5][5]);
        tilesTrue.add(board.getBoard()[6][6]);
        tilesTrue.add(board.getBoard()[7][7]);
        tilesTrue.add(board.getBoard()[2][2]);
        tilesTrue.add(board.getBoard()[1][1]);
        tilesTrue.add(board.getBoard()[0][0]);
        tilesTrue.add(board.getBoard()[4][2]);
        tilesTrue.add(board.getBoard()[5][1]);
        tilesTrue.add(board.getBoard()[6][0]);
        tilesTrue.add(board.getBoard()[2][4]);
        tilesTrue.add(board.getBoard()[1][5]);
        tilesTrue.add(board.getBoard()[0][6]);
        tilesTrue.add(board.getBoard()[3][4]);
        tilesTrue.add(board.getBoard()[3][5]);
        tilesTrue.add(board.getBoard()[3][6]);
        tilesTrue.add(board.getBoard()[3][7]);
        tilesTrue.add(board.getBoard()[3][2]);
        tilesTrue.add(board.getBoard()[3][1]);
        tilesTrue.add(board.getBoard()[3][0]);
        tilesTrue.add(board.getBoard()[2][3]);
        tilesTrue.add(board.getBoard()[1][3]);
        tilesTrue.add(board.getBoard()[0][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard(player);

        List<Tile> tilesGenerated = queenPiece.getTilesToMoveTo();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();

        tilesTrue.add(board.getBoard()[4][4]);
        tilesTrue.add(board.getBoard()[5][5]);
        tilesTrue.add(board.getBoard()[6][6]);
        tilesTrue.add(board.getBoard()[7][7]);
        tilesTrue.add(board.getBoard()[2][2]);
        tilesTrue.add(board.getBoard()[1][1]);
        tilesTrue.add(board.getBoard()[0][0]);
        tilesTrue.add(board.getBoard()[4][2]);
        tilesTrue.add(board.getBoard()[5][1]);
        tilesTrue.add(board.getBoard()[6][0]);
        tilesTrue.add(board.getBoard()[2][4]);
        tilesTrue.add(board.getBoard()[1][5]);
        tilesTrue.add(board.getBoard()[0][6]);
        tilesTrue.add(board.getBoard()[3][4]);
        tilesTrue.add(board.getBoard()[3][5]);
        tilesTrue.add(board.getBoard()[3][6]);
        tilesTrue.add(board.getBoard()[3][7]);
        tilesTrue.add(board.getBoard()[3][2]);
        tilesTrue.add(board.getBoard()[3][1]);
        tilesTrue.add(board.getBoard()[3][0]);
        tilesTrue.add(board.getBoard()[4][3]);
        tilesTrue.add(board.getBoard()[5][3]);
        tilesTrue.add(board.getBoard()[6][3]);
        tilesTrue.add(board.getBoard()[7][3]);
        tilesTrue.add(board.getBoard()[2][3]);
        tilesTrue.add(board.getBoard()[1][3]);
        tilesTrue.add(board.getBoard()[0][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGenerated() {
        queenPiece.getCurrentTile().setPiece(null);
        queenPiece = new QueenPiece(player, board, PieceColor.WHITE, board.getBoard()[3][3]);
        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);

        board.checkBoard(player);

        List<Tile> tilesGenerated = queenPiece.getTilesToMoveTo();

        board.printBoard();

        for (Tile tile : tilesGenerated) System.out.println(tile);
    }
}
