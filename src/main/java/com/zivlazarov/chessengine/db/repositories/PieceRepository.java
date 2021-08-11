package com.zivlazarov.chessengine.db.repositories;

import com.zivlazarov.chessengine.db.dao.PieceDao;
import com.zivlazarov.chessengine.model.pieces.Piece;

import java.sql.SQLException;
import java.util.List;

public class PieceRepository {

    PieceDao pieceDao;

    public PieceRepository(PieceDao pieceDao) {
        this.pieceDao = pieceDao;
    }

    public int insertPiece(Piece piece) throws SQLException {
        return pieceDao.insertPiece(piece);
    }

    public int[] insertAllPieces(List<Piece> pieces) throws SQLException {
        return pieceDao.insertAllPieces(pieces);
    }
}
