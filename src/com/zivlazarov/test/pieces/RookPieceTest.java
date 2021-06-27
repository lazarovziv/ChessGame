package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RookPieceTest {

    private static Board board;
    private static RookPiece rookPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0], 0);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][0], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[0][3], 0);
        board.checkBoard(player);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = rookPiece.getPossibleMoves();
        for (Tile tile : tilesGenerated) System.out.println("[" + tile.getRow() + ", " + tile.getCol() + "]");
        board.printBoard();

        List<Tile> tilesTrue = new ArrayList<>();

        for (int i = 1; i <= opponentPawnPiece.getCurrentTile().getCol(); i++) tilesTrue.add(board.getBoard()[0][i]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        List<Tile> tilesGenerated = rookPiece.getPossibleMoves();

        pawnPiece.moveToTile(board.getBoard()[3][0]);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard(player);

        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[0][1]);
        tilesTrue.add(board.getBoard()[0][2]);
        tilesTrue.add(board.getBoard()[0][3]);
        tilesTrue.add(board.getBoard()[0][4]);
        tilesTrue.add(board.getBoard()[0][5]);
        tilesTrue.add(board.getBoard()[0][6]);
        tilesTrue.add(board.getBoard()[0][7]);
        tilesTrue.add(board.getBoard()[1][0]);
        tilesTrue.add(board.getBoard()[2][0]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGenerated() {
        rookPiece.getCurrentTile().setPiece(null);
        rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[3][3], 0);
        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);

        board.checkBoard(player);

        List<Tile> tilesGenerated = rookPiece.getPossibleMoves();

        board.printBoard();

        for (Tile tile : tilesGenerated) System.out.println(tile);
    }
}
