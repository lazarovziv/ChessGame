package com.zivlazarov.chessengine.db.repositories;

import com.zivlazarov.chessengine.db.dao.MoveDao;
import com.zivlazarov.chessengine.model.move.Move;

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
