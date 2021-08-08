package com.zivlazarov.test.client.pieces;

import com.zivlazarov.chessengine.client.model.pieces.KnightPiece;
import com.zivlazarov.chessengine.client.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.player.Player;
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
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        knightPiece = new KnightPiece(player, board, board.getBoard()[0][1], 0);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[2][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[1][3], 0);
        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = knightPiece.getPossibleMoves();
//        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
//        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[1][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard();

        List<Tile> tilesGenerated = knightPiece.getPossibleMoves();
        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][2]);
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[1][3]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }
}
