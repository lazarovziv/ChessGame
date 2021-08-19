package com.zivlazarov.test.chessengine.pieces;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Test;

public class MinimaxTest {

    private static Board board;
    private static Player player;
    private static Player opponent;
    private static Minimax minimax;

    @Test
    public void testSearch() {
//        board.printBoard();
//        System.out.println(board.evaluateBoard());
//        int value = minimax.search(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE);
//        System.out.println(value);
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        player.setName("White");
        opponent.setName("Black");

        player.setOpponent(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard();

        minimax = new Minimax();

        double value = minimax.search(board, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println(value);
    }

    @Test
    public void testFindBestMove() {
        board = new Board();
        player = new Player(board, PieceColor.WHITE);
        opponent = new Player(board, PieceColor.BLACK);

        player.setName("White");
        opponent.setName("Black");

        player.setOpponent(opponent);

        board.setWhitePlayer(player);
        board.setBlackPlayer(opponent);

        board.setCurrentPlayer(player);

        board.initBoard();

        board.checkBoard();

        minimax = new Minimax();

        Move move = minimax.findBestMove(board, 4, player);
        System.out.println(move);
        move.makeMove(true, true);
        board.printBoard();

        Move oMove = minimax.findBestMove(board, 4, opponent);
        System.out.println(oMove);
        oMove.makeMove(true, true);
        board.printBoard();
    }
}
