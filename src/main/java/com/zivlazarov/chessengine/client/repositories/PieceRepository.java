package com.zivlazarov.chessengine.client.repositories;

import com.zivlazarov.chessengine.client.db.PieceDao;
import com.zivlazarov.chessengine.client.model.pieces.Piece;

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
