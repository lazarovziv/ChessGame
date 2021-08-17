package com.zivlazarov.test.pieces;

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
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponent(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        board.setCurrentPlayer(player);
        KingPiece kingPiece = new KingPiece(player, board, board.getBoard()[7][4]);
        KingPiece opponentKing = new KingPiece(opponent, board, board.getBoard()[0][4]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[1][0], 0);
        knightPiece = new KnightPiece(player, board, board.getBoard()[3][0], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[2][1], 0);
        board.checkBoard();
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
        board.checkBoard();

        List<Tile> tilesGenerated = pawnPiece.getPossibleMoves();
        board.printBoard();
        List<Tile> tilesTrue = new ArrayList<>();
        tilesTrue.add(board.getBoard()[2][0]);
        tilesTrue.add(board.getBoard()[3][0]);

        Assertions.assertEquals(tilesTrue, tilesGenerated);
    }

    @Test
    public void testEnPassant() {
        PawnPiece pawn = new PawnPiece(player, board, board.getBoard()[3][4], 0);
        PawnPiece opponentPawn = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawn)
                .targetTile(board.getBoard()[4][4])
                .build();
        move.makeMove(true, true);

        Move oMove = new Move.Builder()
                .board(board)
                .player(opponent)
                .movingPiece(opponentPawn)
                .targetTile(board.getBoard()[4][3])
                .build();
        oMove.makeMove(true, true);

        Move knightMove = new Move.Builder()
                .board(board).player(player).movingPiece(knightPiece).targetTile(board.getBoard()[1][1]).build();
        knightMove.makeMove(true, true);

        Move opponentPawnPieceMove = new Move.Builder()
                .board(board).player(opponent).movingPiece(opponentPawnPiece).targetTile(board.getBoard()[1][0]).build();
        opponentPawnPieceMove.makeMove(true, true);

        Move enPassant = new Move.Builder()
                .board(board).player(player).movingPiece(pawn).targetTile(board.getBoard()[5][3]).build();
        enPassant.makeMove(true, true);

        board.printBoard();
    }

    @Test
    public void testPawnPromotion() {
        Piece pawn = new PawnPiece(player, board, board.getBoard()[6][3], 6);
        board.printBoard();
        board.checkBoard();

        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawn)
                .targetTile(board.getBoard()[7][3])
                .build();

        move.makeMove(true, true);

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
