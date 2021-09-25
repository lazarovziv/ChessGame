package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.ai.Minimax;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;

public class BoardController {

    //

    private Board board;
    private Player whitePlayer;
    private Player blackPlayer;
    private Minimax minimax;

    private int difficulty;

    public BoardController() {
        whitePlayer = new Player(PieceColor.WHITE);
        blackPlayer = new Player(PieceColor.BLACK);

        minimax = new Minimax();

        whitePlayer.setName("White Player");
        blackPlayer.setName("Black Player");

        whitePlayer.setAI(false);
        blackPlayer.setAI(false);

        minimax = new Minimax();

        whitePlayer.setOpponent(blackPlayer);

        board = new Board();

        board.setWhitePlayer(whitePlayer);
        board.setBlackPlayer(blackPlayer);

        whitePlayer.setBoard(board);
        blackPlayer.setBoard(board);

        board.setCurrentPlayer(whitePlayer);

        board.initBoard();
        board.checkBoard();
    }

    public Board getBoard() {
        return board;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public Minimax getMinimax() {
        return minimax;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
