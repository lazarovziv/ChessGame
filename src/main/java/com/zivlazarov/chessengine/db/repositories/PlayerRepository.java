package com.zivlazarov.chessengine.db.repositories;

import com.zivlazarov.chessengine.db.dao.PlayerDao;
import com.zivlazarov.chessengine.model.player.Player;

import java.sql.SQLException;

public class PlayerRepository {

    // TODO: add a dependency injection library
    PlayerDao playerDao;

    // simple constructor injection
    public PlayerRepository(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    public int insertPlayer(Player player) throws SQLException {
        return playerDao.insertPlayer(player);
    }

    public int deletePlayer(Player player) throws SQLException {
        return playerDao.deletePlayer(player);
    }

    public Player findPlayerByID(int id) throws SQLException {
        return playerDao.findPlayerByID(id);
    }
}