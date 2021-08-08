package com.zivlazarov.test.client.pieces;

import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.pieces.KingPiece;
import com.zivlazarov.chessengine.client.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.client.model.pieces.Piece;
import com.zivlazarov.chessengine.client.model.player.Player;
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
        Piece king = new KingPiece(player, board, board.getBoard()[5][5]);
        Piece oKing = new KingPiece(opponent, board, board.getBoard()[7][7]);
        pawnPiece = new PawnPiece(player, board, board.getBoard()[1][2], 0);
        opponentPawnPiece = new PawnPiece(opponent, board, board.getBoard()[2][3], 0);

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
