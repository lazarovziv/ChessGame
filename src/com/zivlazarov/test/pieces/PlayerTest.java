package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    private static Board board;
    private static PawnPiece pawnPiece;
    private static Player player;
    private static Player opponent;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);
        player.setOpponentPlayer(opponent);
        opponent.setOpponentPlayer(player);
        pawnPiece = new PawnPiece(player, board, player.getPlayerColor(), board.getBoard()[0][3], 0);
    }

    @Test
    public void testMove() {
        board.printBoard();
        pawnPiece.refresh();
        if (player.movePiece(pawnPiece, board.getBoard()[1][3])) board.printBoard();
    }
}
