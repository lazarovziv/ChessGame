package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.pieces.QueenPiece;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.zivlazarov.chessengine.db.DatabaseUtils.*;

public class PieceDao implements Dao {

    public int insertPiece(Piece piece) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        int id = piece.getId();
        long playerID = piece.getPlayer().getId();
        String name = piece.getName();
        int pieceCounter = (piece instanceof KingPiece || piece instanceof QueenPiece) ? piece.getPieceCounter() : -1;
        boolean isAlive = piece.isAlive();
        int tileID = piece.getCurrentTile().getId();
        PieceColor pieceColor = piece.getPieceColor();
        int color = pieceColor == PieceColor.WHITE ? 1 : 0;

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "INSERT INTO Piece (id, player_id, name, pieceCounter, isAlive, tile_id, pieceColor) " +
                    "VALUES (" + id + ", " +
                    playerID + ", " +
                    name + ", " +
                    pieceCounter + ", " +
                    isAlive + ", " +
                    tileID + ", " +
                    color + ");";

            statement.executeUpdate(query);

            System.out.println("Insertion complete.");

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
}
