package com.zivlazarov.chessengine.logs;

import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.Player;

import java.util.HashMap;

class MovesLog {

    // hashmap of <Piece, Tile> of piece moved and tile it moved to
    final private HashMap<Player, Piece> log;

    private static MovesLog instance;

    private MovesLog() {
        log = new HashMap<>();
    }

    public MovesLog getInstance() {
        if (instance == null) instance = new MovesLog();
        return instance;
    }

    public HashMap<Player, Piece> getLog() {
        return log;
    }
}