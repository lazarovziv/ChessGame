package com.zivlazarov.test.client.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

public class MoveTest {

    private static Board board;

    private static Player player;
    private static Player opponent;

    private static PawnPiece pawnPiece;
    private static PawnPiece opponentPawnPiece;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][5]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[4][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[6][3], 0);

        board.checkBoard();
    }

    @Test
    public void testMove() {
        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(opponentPawnPiece.getCurrentTile())
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();
    }

    @Test
    public void testUnmakeMove() {
        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + player.getPlayerDirection()][pawnPiece.getCurrentTile().getCol()])
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();

        System.out.println(board.getCurrentPlayer());

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(player.getKing())
                .targetTile(board.getBoard()[4][4])
                .build();

        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();

        System.out.println(board.getCurrentPlayer());
    }

    @Test
    public void testUnmakeMoveEnPassant() {
        board.printBoard();
        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        move.makeMove(true);
        board.printBoard();

        Move oMove = new Move.Builder()
                .board(board)
                .player(opponent)
                .movingPiece(opponentPawnPiece)
                .targetTile(board.getBoard()[opponentPawnPiece.getCurrentTile().getRow() - 2][opponentPawnPiece.getCurrentTile().getCol()])
                .build();
        oMove.makeMove(true);
        board.printBoard();

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[opponentPawnPiece.getCurrentTile().getRow() + 1][opponentPawnPiece.getCurrentTile().getCol()])
                .build();
        move.makeMove(true);
        board.printBoard();

        move.unmakeMove(true);
        board.printBoard();
    }

    @Test
    public void testUnmakePawnPromotion() {
        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        move.makeMove(true);
        board.printBoard();

        player.getAlivePieces().forEach(System.out::println);

        move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(pawnPiece)
                .targetTile(board.getBoard()[pawnPiece.getCurrentTile().getRow() + 1][pawnPiece.getCurrentTile().getCol()])
                .build();

        move.makeMove(true);
        board.printBoard();

        player.getAlivePieces().forEach(System.out::println);

        move.unmakeMove(true);
        board.printBoard();

        player.getAlivePieces().forEach(System.out::println);
    }

    @Test
    public void testMoveEquals() {
        Move move = new Move.Builder()
                .board(board)
                .player(player)
                .movingPiece(player.getKing())
                .targetTile(board.getBoard()[6][4])
                .build();

        Iterator<Move> iterator = player.getMoves().iterator();

        Assertions.assertTrue(move.equals(iterator.next()));
    }
}
