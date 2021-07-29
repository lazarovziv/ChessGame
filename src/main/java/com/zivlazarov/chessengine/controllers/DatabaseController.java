package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.db.PlayerDao;
import com.zivlazarov.chessengine.model.player.Player;

public class DatabaseController {

    private final PlayerDao playerDao;

    public DatabaseController(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public void insertPlayer(Player player) {
        playerDao.insertPlayer(player);
    }

    public Player findPlayerByID(int id) {
        return playerDao.findPlayerByID(id);
    }
}
