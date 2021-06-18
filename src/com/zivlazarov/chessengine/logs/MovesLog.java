package com.zivlazarov.chessengine.logs;

import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.player.Player;
import com.zivlazarov.chessengine.model.utils.board.Tile;

import java.util.Stack;

public class MovesLog {

    // hashmap of <Piece, Tile> of piece moved and tile it moved to
    private static Stack<Pair<Player, Pair<Tile, Tile>>> movesLog;

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

    public static Stack<Pair<Player, Pair<Tile, Tile>>> getMovesLog() {
        return movesLog;
    }
}