package com.zivlazarov.chessengine.client.db;

import com.zivlazarov.chessengine.client.model.move.Move;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.zivlazarov.chessengine.client.db.DatabaseUtils.*;

public class MoveDao implements Dao {

    public int insertMove(Move move) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        int pieceID = move.getMovingPiece().getId();
        int tileID = move.getTargetTile().getId();
        int playerID = move.getPlayer().getId();

        move.setId(pieceID * move.getPlayer().getPlayerDirection() * playerID);
        int id = move.getId();

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "INSERT INTO Move (id, piece_id, tile_id, player_id) VALUES (" + id + ", " +
                    pieceID + ", " +
                    tileID + ", " +
                    playerID + ");";

            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.close();
            if (statement != null)
                statement.close();
        }
        return id;
    }
}
