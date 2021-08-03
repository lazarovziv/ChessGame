package com.zivlazarov.chessengine.client.db;

import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.board.TileColor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.zivlazarov.chessengine.client.db.DatabaseUtils.*;

public class TileDao {

    public int insertTile(Tile tile) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        int id = tile.getId();
        int row = tile.getRow();
        int col = tile.getCol();
        boolean isEmpty = tile.isEmpty();
        boolean isThreatenedByWhite = tile.isThreatenedByWhite();
        boolean isThreatenedByBlack = tile.isThreatenedByBlack();
        int pieceID = tile.getPiece() == null ? 0 : tile.getPiece().getId();
        TileColor tileColor = tile.getTileColor();
        int color = tileColor == TileColor.WHITE ? 1 : 0;

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "INSERT INTO Tile (id, tile_row, tile_col, isEmpty, isThreatenedByWhite, isThreatenedByBlack, piece_id, tileColor) " +
                    "VALUES (" + id + ", " +
                    row + ", " +
                    col + ", " +
                    isEmpty + ", " +
                    isThreatenedByWhite + ", " +
                    isThreatenedByBlack + ", " +
                    pieceID + ", " +
                    color + ");";

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

    public int deleteTile(Tile tile) throws SQLException {

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement statement = connection.createStatement()) {

            String query = "DELETE FROM Tile WHERE id = " + tile.getId();

            statement.executeUpdate(query);

            System.out.println("Deletion completed.");
        } catch (SQLException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        }

        return tile.getId();
    }
}
