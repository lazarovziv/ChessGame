package com.zivlazarov.chessengine.client.db;

import com.zivlazarov.chessengine.client.model.board.PieceColor;
import com.zivlazarov.chessengine.client.model.pieces.Piece;
import com.zivlazarov.chessengine.client.model.pieces.PieceType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static com.zivlazarov.chessengine.client.db.DatabaseUtils.*;

public class PieceDao implements Dao {

    public int insertPiece(Piece piece) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        int id = piece.getId();
        long playerID = piece.getPlayer().getId();
        int pieceCounter = piece.getPieceCounter();
        boolean isAlive = piece.isAlive();
        int tileID = piece.getCurrentTile().getId();
        PieceColor pieceColor = piece.getPieceColor();
        int color = pieceColor == PieceColor.WHITE ? 1 : 0;
        PieceType pieceType = piece.getPieceType();
        int type = pieceTypeMap.get(pieceType);

//        int type = -1;
//        switch (pieceType) {
//            case PAWN -> type = 0;
//            case BISHOP -> type = 1;
//            case KNIGHT ->  type = 2;
//            case ROOK -> type = 3;
//            case QUEEN -> type = 4;
//            case KING -> type = 5;
//        }

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "INSERT INTO Piece(id, player_id, pieceCounter, isAlive, tile_id, pieceColor, type) " +
                    "VALUES (" + id + ", " +
                    playerID + ", " +
//                    name + ", " +
                    pieceCounter + ", " +
                    isAlive + ", " +
                    tileID + ", " +
                    color + ", " +
                    type + ");";

            statement.executeUpdate(query);

        } catch (SQLException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        } finally {
            if (statement != null)
                statement.close();
            if (connection != null)
                connection.close();
        }
        return id;
    }

    public int[] insertAllPieces(List<Piece> pieces) {
        int[] piecesIDs = new int[pieces.size()];
        for (int i = 0; i < piecesIDs.length; i++) {
            try {
                insertPiece(pieces.get(i));
                piecesIDs[i] = pieces.get(i).getId();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return piecesIDs;
    }
}
