package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Player;
import com.zivlazarov.chessengine.model.utils.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.ArrayList;
import java.util.List;

public class KingPieceTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static Player player;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
//        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[3][4], 0);
        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[0][3]);
//        pawnPiece = new PawnPiece(board, PieceColor.WHITE, board.getBoard()[2][4], 0);
//        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[1][4]);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[2][4], 0);
        board.checkBoard(player);
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
        tilesTrue.add(board.getBoard()[kingRow+1][kingCol]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[1][4]);
//        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[2][4], 0);
//        opponentPawnPiece.moveToTile(board.getBoard()[opponentPawnPiece.getCurrentTile().getRow() - 1][opponentPawnPiece.getCurrentTile().getCol()]);
        board.checkBoard(player);

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

    @Test
    public void testCastling() {
//        kingPiece.getCurrentTile().setPiece(null);
//        pawnPiece.getCurrentTile().setPiece(null);
//        opponentPawnPiece.getCurrentTile().setPiece(null);
//        kingPiece = new KingPiece(board, PieceColor.WHITE, board.getBoard()[0][3]);
        RookPiece rookPiece = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][7], 0);
        RookPiece rookPiece1 = new RookPiece(board, PieceColor.WHITE, board.getBoard()[0][0], 1);
        RookPiece blackRook0 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][0], 0);
        RookPiece blackRook1 = new RookPiece(board, PieceColor.BLACK, board.getBoard()[7][7], 1);
        board.checkBoard(player);

        List<Tile> tilesGenerated = kingPiece.getTilesToMoveTo();
        board.printBoard();

        tilesGenerated.forEach(System.out::println);

        Assertions.assertTrue(tilesGenerated.contains(board.getBoard()[0][1]));
        Assertions.assertTrue(tilesGenerated.contains(board.getBoard()[0][5]));

        PlayerController controller = new PlayerController(player, new Player(board, PieceColor.WHITE));
        controller.kingSideCastle(kingPiece, rookPiece1);
        board.checkBoard(player);
        board.printBoard();

    }

}
