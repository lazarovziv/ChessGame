package com.zivlazarov.chessengine.client.repositories;

import com.zivlazarov.chessengine.client.db.TileDao;
import com.zivlazarov.chessengine.client.model.board.Tile;

import java.sql.SQLException;

public class TileRepository {

    TileDao tileDao;

    public TileRepository(TileDao tileDao) {
        this.tileDao = tileDao;
    }

    public int insertTile(Tile tile) throws SQLException {
        return tileDao.insertTile(tile);
    }

    public int deleteTile(Tile tile) throws SQLException {
        return tileDao.deleteTile(tile);
    }
}
