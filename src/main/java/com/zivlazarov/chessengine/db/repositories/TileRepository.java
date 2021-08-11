package com.zivlazarov.chessengine.db.repositories;

import com.zivlazarov.chessengine.db.dao.TileDao;
import com.zivlazarov.chessengine.model.board.Tile;

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
