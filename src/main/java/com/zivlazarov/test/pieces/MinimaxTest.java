package com.zivlazarov.test.pieces;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.PipedDeepCopy;
import kotlin.Triple;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MinimaxTest {

    private static Board board;
    private static Player player;
    private static Player opponent;
    private static Minimax minimax;

    @Test
    public void testSearch() {
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

        double value = minimax.search(board, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        System.out.println(value);
    }

    @Test
    public void testFindBestMove() {
//        for (Move move : player.getMoves()) System.out.println(move)

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

        Move move = minimax.findBestMove(board, 3, player);
        System.out.println(move);
        move.makeMove(true, true);
        Move oMove = minimax.findBestMove(board, 3, opponent);
        System.out.println(oMove);
    }

    @Test
    public void testDeepCopy() {
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

//        Board newBoard = new Board();
//
//        Player newWhite = player.clone();
//        Player newBlack = opponent.clone();
//
//        newWhite.setOpponent(newBlack);
//        newBoard.setWhitePlayer(newWhite);
//        newBoard.setBlackPlayer(newBlack);
//
//        newWhite.setBoard(newBoard);
//        newBlack.setBoard(newBoard);
//
//        newBoard.setCurrentPlayer(newWhite);
//
//        new ArrayList<Piece>(player.getAlivePieces()).forEach(piece -> piece.clone(newBoard, newWhite));
//        new ArrayList<Piece>(opponent.getAlivePieces()).forEach(piece -> piece.clone(newBoard, newBlack));

        Triplet<Board, Player, Player> triplet = Board.createNewBoard(player, opponent, board.getCurrentPlayer());

        Board newBoard = triplet.getValue0();
        Player newWhite = triplet.getValue1();
        Player newBlack = triplet.getValue2();

//        newBoard.printBoard();
        newBoard.checkBoard();
        for (Piece piece : newBoard.getCurrentPlayer().getAlivePieces()) {
            System.out.println(piece.getPieceIndex());
        }
        newBoard.getCurrentPlayer().getMoves().forEach(System.out::println);

        Move move = (Move) newWhite.getMoves().toArray()[0];
        move.makeMove(true, true);
        newBoard.printBoard();
        board.printBoard();
    }
}
