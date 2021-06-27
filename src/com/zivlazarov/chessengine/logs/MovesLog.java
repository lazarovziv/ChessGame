package com.zivlazarov.chessengine.logs;

import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.board.Tile;

import java.util.Stack;

public class MovesLog {

    private static Stack<Pair<Pair<Player, Piece>, Pair<Tile, Tile>>> movesLog;

    private static MovesLog instance;

    private MovesLog() {
        movesLog = new Stack<>();
    }

    public static MovesLog getInstance() {
        if (instance == null) {
            instance = new MovesLog();
        }
        return instance;
    }

    public Stack<Pair<Pair<Player, Piece>, Pair<Tile, Tile>>> getMovesLog() {
        return movesLog;
    }
}