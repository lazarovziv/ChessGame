package com.zivlazarov.test.pieces;

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
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);

        board.setCurrentPlayer(player);
        Piece king = new KingPiece(player, board, player.getPlayerColor(), board.getBoard()[5][5], true);
        Piece oKing = new KingPiece(opponent, board, opponent.getPlayerColor(), board.getBoard()[7][7], true);
        pawnPiece = new PawnPiece(player, board, PieceColor.WHITE, board.getBoard()[1][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, PieceColor.BLACK, board.getBoard()[2][3], 0);

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
                .targetTile(opponentPawnPiece.getCurrentTile())
                .build();

        board.printBoard();
        move.makeMove(true);
        board.printBoard();
        move.unmakeMove(true);
        board.printBoard();
        System.out.println(opponentPawnPiece.getPlayer().getPlayerColor());
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
