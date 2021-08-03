package com.zivlazarov.test.client.pieces;

import com.zivlazarov.chessengine.client.model.ai.Minimax;
import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.move.Move;
import com.zivlazarov.chessengine.client.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MinimaxTest {

    private static Board board;
    private static Player player;
    private static Player opponent;
    private static Minimax minimax;

    @BeforeAll
    public static void setup() {
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        player.setOpponentPlayer(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard();

        minimax = new Minimax(board);
    }

    @Test
    public void testSearch() {
//        board.printBoard();
//        System.out.println(board.evaluateBoard());
        int value = minimax.search(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println(value);
    }

    @Test
    public void testExecute() {
        Move bestMove = minimax.execute(3, board.getCurrentPlayer().getPlayerColor() == PieceColor.WHITE);
        System.out.println(bestMove);
    }
}
