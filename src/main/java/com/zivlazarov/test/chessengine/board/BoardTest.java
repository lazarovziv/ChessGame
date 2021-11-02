package com.zivlazarov.test.chessengine.board;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BoardTest {

    @Test
    public void testDepth4() {
        Board board = new Board();
        Player white = new Player(PieceColor.WHITE);
        Player black = new Player(PieceColor.BLACK);
        white.setOpponent(black);
        white.setBoard(board);
        black.setBoard(board);
        board.setWhitePlayer(white);
        board.setBlackPlayer(black);
        board.setCurrentPlayer(white);
        board.initBoard();
        board.checkBoard();

        System.out.println(generateMoves(board, 4));
    }

    public int generateMoves(Board board, int depth) {
        if (depth == 0) return 1;

        int generatedMoves = 0;
        List<Move> moves = board.getCurrentPlayer().getMoves().stream().toList();

        for (Move move : moves) {
            move.makeMove(true, true);
            generatedMoves += generateMoves(board, depth - 1);
            move.unmakeMove(false);
        }
        return generatedMoves;
    }

}
