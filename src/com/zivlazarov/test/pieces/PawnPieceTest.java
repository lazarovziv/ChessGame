package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;
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
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        opponent.setOpponentPlayer(player);
        KingPiece kingPiece = new KingPiece(player, board, player.getPlayerColor(), board.getBoard()[7][4]);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][0], 0);
        knightPiece = new KnightPiece(player, board, PieceColor.WHITE, board.getBoard()[3][0], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[2][1], 0);
        board.checkBoard(player);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenAPieceInterferes() {
        List<Tile> tilesGenerated = pawnPiece.getPossibleMoves();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(opponentPawnPiece.getCurrentTile());

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testWhatTilesAreBeingGeneratedWhenNoPieceInterferes() {
        knightPiece.getCurrentTile().setPiece(null);
        opponentPawnPiece.getCurrentTile().setPiece(null);
        board.checkBoard(player);

        List<Tile> tilesGenerated = pawnPiece.getPossibleMoves();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[3][0]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testEnPassant() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        PawnPiece pawn = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][3], 0);
        PawnPiece opponentPawn = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[6][3], 0);
//        KingPiece whiteKing = new KingPiece(player, board, PieceColor.WHITE, board.getBoard()[0][0]);
//        KingPiece blackKing = new KingPiece(opponent, board, PieceColor.BLACK, board.getBoard()[7][7]);

        board.checkBoard(player);
        PlayerController controller = new PlayerController();
        controller.setPlayer(player);
        controller.setOpponentPlayer(opponent);
        controller.addPieceToAlive(pawn);
        controller.movePiece(pawn, board.getBoard()[3][3]);
        board.checkBoard(player);
        board.printBoard();
        controller.setPlayer(opponent);
        controller.addPieceToAlive(opponentPawn);
        controller.movePiece(opponentPawn, board.getBoard()[4][3]);
        board.checkBoard(player);
        board.printBoard();
        controller.setPlayer(player);
        board.checkBoard(player);

        pawn.getPossibleMoves().forEach(tile -> System.out.print(tile + ","));
    }

    @Test
    public void testPromotion() {
        ChessPiece pawn = new ChessPiece(player, board, PieceType.PAWN, PieceColor.WHITE, board.getBoard()[6][3]);
        board.printBoard();
        board.checkBoard(player);

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawn)
                .targetTile(board.getBoard()[7][3])
                .build();

        move.makeMove(true);
        board.printBoard();
    }

    @Test
    public void testPawnPromotion() {
        Piece pawn = new PawnPiece(player, board, player.getPlayerColor(), board.getBoard()[6][3], 6);
        board.printBoard();
        board.checkBoard(player);

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawn)
                .targetTile(board.getBoard()[7][3])
                .build();

        move.makeMove(true);

//        player.addPieceToDead(pawn);
//
//        pawn = new QueenPiece(player, board, player.getPlayerColor(), board.getBoard()[7][3]);
        board.printBoard();
//        board.checkBoard(player);
        pawn.getPossibleMoves().forEach(System.out::println);
    }

    @Test
    public void testFirstMove() {
        board.printBoard();
        for (Tile tile : pawnPiece.getPossibleMoves()) System.out.println(tile);
    }
}
