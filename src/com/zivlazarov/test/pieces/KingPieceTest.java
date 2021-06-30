package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.KnightPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.board.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class KingPieceTest {

    private static Board board;
    private static KingPiece kingPiece;
    private static KingPiece opponentKingPiece;
    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;
    private static KnightPiece opponentKnightPiece;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        opponent.setOpponentPlayer(player);
        player.setOpponentPlayer(opponent);
//        opponentPawnPiece = new PawnPiece(board, PieceColor.BLACK, board.getBoard()[3][4], 0);
        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[0][3]);
        opponentKingPiece = new KingPiece(opponent, board, PieceColor.BLACK, board.getBoard()[7][4]);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[1][3], 0);
        opponentKnightPiece = new KnightPiece(opponent, board, PieceColor.BLACK, board.getBoard()[4][1], 0);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][1], 0);
//        board.checkBoard();
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[1][4]);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[2][4], 0);
        board.checkBoard(player);
        List<Tile> tilesGenerated = kingPiece.getPossibleMoves();
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
        kingPiece = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[1][4]);
//        pawnPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[2][4], 0);
//        opponentPawnPiece.moveToTile(board.getBoard()[opponentPawnPiece.getCurrentTile().getRow() - 1][opponentPawnPiece.getCurrentTile().getCol()]);
        board.checkBoard(player);

        List<Tile> tilesGenerated = kingPiece.getPossibleMoves();
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
        RookPiece rookPiece = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][7], true, 0);
        RookPiece rookPiece1 = new RookPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0], false, 1);
        RookPiece blackRook0 = new RookPiece(opponent, board, PieceColor.BLACK, board.getBoard()[7][0], true, 0);
        RookPiece blackRook1 = new RookPiece(opponent, board, PieceColor.BLACK, board.getBoard()[7][7], false, 1);
        board.checkBoard(player);

        List<Tile> tilesGenerated = kingPiece.getPossibleMoves();
        board.printBoard();

        tilesGenerated.forEach(System.out::println);

        Assertions.assertTrue(tilesGenerated.contains(board.getBoard()[0][1]));
        Assertions.assertTrue(tilesGenerated.contains(board.getBoard()[0][5]));

        PlayerController controller = new PlayerController();
        controller.setPlayer(player);
        controller.kingSideCastle(kingPiece, rookPiece1);
        board.checkBoard(player);
        board.printBoard();

    }

    @Test
    public void testUnmakeMove() {
        board.printBoard();
        board.checkBoard(player);
        kingPiece.moveToTile(board.getBoard()[1][3]);
        board.printBoard();
        kingPiece.unmakeLastMove();
        board.printBoard();
        board.checkBoard(player);
        for (Tile tile : kingPiece.getPossibleMoves()) System.out.println(tile);
    }

    @Test
    public void testCheckSituation() {
        board.removePieceFromBoard(opponentPawnPiece);
        board.checkBoard(opponent);
        board.printBoard();

        opponentKnightPiece.moveToTile(board.getBoard()[2][2]);
        board.checkBoard(player);
        if (player.isInCheck()) System.out.println("Check! ");
        board.printBoard();
        pawnPiece.moveToTile(board.getBoard()[2][1]);
        board.checkBoard(opponent);
        board.printBoard();
    }

}
