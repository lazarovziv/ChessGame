package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.util.Stack;

public class BoardController {

    private Board board;

    public BoardController(Board board) {
        this.board = board;
    }

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

    public void checkBoard() {
        board.checkBoard();
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

    public Stack<Pair<Piece, Tile>> getGameHistoryMoves() {
        return board.getGameHistoryMoves();
    }

    public void addObserver(MyObserver observer) {
        board.addObserver(observer);
    }

    public void handleGameSituation(Player currentPlayer, int turn) {

    }
}
