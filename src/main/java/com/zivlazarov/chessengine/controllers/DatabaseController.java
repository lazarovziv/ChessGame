package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.db.PlayerDao;
import com.zivlazarov.chessengine.model.player.Player;

import java.sql.SQLException;

public class DatabaseController {

    private final PlayerDao playerDao;

    public DatabaseController(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public void insertPlayer(Player player) {
        try {
            playerDao.insertPlayer(player);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public Player findPlayerByID(int id) {
//        return playerDao.findPlayerByID(id);
//    }
}
