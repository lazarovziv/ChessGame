package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;

public class MoveController {

    private Player player;
    private Move move;

    public MoveController(Player player) {
        this.player = player;
    }

    public boolean makeMove() {
        // setting both arguments to true because it's for UI game
        return move.makeMove(true, true);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }
}
