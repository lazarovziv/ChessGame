package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.BishopPiece;
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

public class BishopPieceTest {

    private static BishopPiece bishopPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static Board board;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        bishopPiece = new BishopPiece(player, board, PieceColor.WHITE, board.getBoard()[0][2], 0);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][3], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[1][1], 0);
        board.checkBoard(player);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = bishopPiece.getPossibleMoves();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[1][1]);
        tilesTrue.add(board.getBoard()[2][0]);

        board.printBoard();

//        Assertions.assertEquals(tilesGenerated.size(), tilesTrue.size());
        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        pawnPiece.moveToTile(board.getBoard()[2][3]);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard(player);

        List<Tile> tilesGenerated = bishopPiece.getPossibleMoves();
        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[1][3]);
        tilesTrue.add(board.getBoard()[2][4]);
        tilesTrue.add(board.getBoard()[3][5]);
        tilesTrue.add(board.getBoard()[4][6]);
        tilesTrue.add(board.getBoard()[5][7]);
        tilesTrue.add(board.getBoard()[1][1]);
        tilesTrue.add(board.getBoard()[2][0]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testIfPieceMovedToTile() {
        Tile tile = board.getBoard()[2][0];
        bishopPiece.moveToTile(tile);

        Assertions.assertEquals(tile, bishopPiece.getCurrentTile());
    }

    @Test
    public void testWhichTilesAreBeingGenerated() {
        opponentPawnPiece.getCurrentTile().setPiece(null);
        pawnPiece.getCurrentTile().setPiece(null);
        bishopPiece.moveToTile(board.getBoard()[3][5]);
        board.checkBoard(player);

        List<Tile> tilesGenerated = bishopPiece.getPossibleMoves();
        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
        board.printBoard();
    }

    @Test
    public void testWhatTilesAreBeingGenerated() {
        bishopPiece.getCurrentTile().setPiece(null);
        bishopPiece = new BishopPiece(player, board, PieceColor.WHITE, board.getBoard()[3][3], 0);
        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);

        board.checkBoard(player);

        List<Tile> tilesGenerated = bishopPiece.getPossibleMoves();

        board.printBoard();

        for (Tile tile : tilesGenerated) System.out.println(tile);
    }
}
