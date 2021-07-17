package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
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
        board = Board.getInstance();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        player.setOpponentPlayer(opponent);
        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);
        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard(board.getCurrentPlayer());

        minimax = new Minimax(player);
    }

    @Test
    public void testSearch() {
        board.printBoard();
        int value = minimax.search(board, 10, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println(value);
    }
}
