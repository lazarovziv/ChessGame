package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.player.Player;

public class BoardController {

    private Board board;

    public BoardController() {}

    public void printBoard() {
        board.printBoard();
    }

    public void printBoard(Tile tileChosen) {
        board.printBoard(tileChosen);
    }

    public void printBoardUpsideDown() {
        board.printBoardUpsideDown();
    }

    public void printBoardUpsideDown(Tile tileChosen) {
        board.printBoardUpsideDown(tileChosen);
    }

    public void checkBoard(Player currentPlayer) {
        board.checkBoard(currentPlayer);
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    public void setWhitePlayer(Player whitePlayer) {
        board.setWhitePlayer(whitePlayer);
    }

    public void setBlackPlayer(Player blackPlayer) {
        board.setBlackPlayer(blackPlayer);
    }
}
