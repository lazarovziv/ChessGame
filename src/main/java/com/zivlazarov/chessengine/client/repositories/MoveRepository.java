package com.zivlazarov.chessengine.client.repositories;

import com.zivlazarov.chessengine.client.db.MoveDao;
import com.zivlazarov.chessengine.client.model.move.Move;

import java.sql.SQLException;

public class MoveRepository {

    MoveDao moveDao;

    public MoveRepository(MoveDao moveDao) {
        this.moveDao = moveDao;
    }

    public int insertMove(Move move) throws SQLException {
        return moveDao.insertMove(move);
    }
}
