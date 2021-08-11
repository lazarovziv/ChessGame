package com.zivlazarov.test.client.pieces;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MinimaxTest {

    private static Board board;
    private static Player player;
    private static Player opponent;
    private static Minimax minimax;

    @BeforeAll
    public static void setup() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        player.setName("White");
        opponent.setName("Black");

        player.setOpponentPlayer(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard();

        minimax = new Minimax();
    }

    @Test
    public void testSearch() {
//        board.printBoard();
//        System.out.println(board.evaluateBoard());
//        int value = minimax.search(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println(value);
        int value = minimax.search(board, 3, board.getCurrentPlayer());
        System.out.println(value);
    }

    @Test
    public void testExecute() {
        for (Move move : player.getMoves()) System.out.println(move);

        Move move = minimax.calculate(board, 3, board.getCurrentPlayer());
        System.out.println(move);
    }
}
