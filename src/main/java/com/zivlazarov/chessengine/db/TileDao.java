package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.board.TileColor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.*;

import static com.zivlazarov.chessengine.db.DatabaseUtils.*;

public class TileDao {

//    public void insertTile(Tile tile) {
//        Connection connection = null;
//        Statement statement = null;
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            statement = connection.createStatement();
//
//            String query = "INSERT INTO tile(tile_row, tile_col, tile_color, is_empty, is_threatened_by_black, is_threatened_by_white) " +
//                    "VALUES (" + tile.getRow() + ", " +
//                    tile.getCol() + ", " +
//                    tile.getTileColor().toString() + ", " +
//                    tile.isEmpty() + ", " +
//                    tile.isThreatenedByBlack() + ", " +
//                    tile.isThreatenedByWhite() + ");";
//
//            statement.executeUpdate(query);
//
//            System.out.println("Tile insertion completed.");
//        } catch (SQLException | ClassNotFoundException exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    public Tile findTileByRowAndCol(int row, int col) {
//        Tile tile = null;
//
//        Connection connection = null;
//        Statement statement = null;
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            statement = connection.createStatement();
//
//            String query = "SELECT tile_row, tile_col, tile_color, is_empty, is_threatened_by_black, is_threatened_by_white FROM tile " +
//                    "WHERE tile_row = " + row + " AND tile_col = " + col + ";";
//
//            ResultSet resultSet = statement.executeQuery(query);
//
//            int tileRow = resultSet.getInt("tile_row");
//            int tileCol = resultSet.getInt("tile_col");
//            String tileColorStr = resultSet.getString("tile_color");
//            TileColor tileColor;
//            if (tileColorStr.equals("WHITE")) tileColor = TileColor.WHITE;
//            else tileColor = TileColor.BLACK;
//
//            boolean threatenedByBlack = resultSet.getBoolean("is_threatened_by_black");
//            boolean threatenedByWhite = resultSet.getBoolean("is_threatened_by_white");
////            boolean isEmpty = resultSet.getBoolean("is_empty");
//
//            tile = new Tile(tileRow, tileCol, tileColor);
//
//            tile.setThreatenedByBlack(threatenedByBlack);
//            tile.setThreatenedByWhite(threatenedByWhite);
//
//            System.out.println("Tile insertion completed.");
//        } catch (SQLException | ClassNotFoundException exception) {
//            exception.printStackTrace();
//        }
//
//        return tile;
//    }
//
//    public void deleteTile(Tile tile) {
//        Connection connection;
//        Statement statement;
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            statement = connection.createStatement();
//
//            String query = "DELETE FROM tile WHERE tile_row = " + tile.getRow() + " AND tile_col = " + tile.getCol() + ";";
//
//            statement.executeUpdate(query);
//
//            System.out.println("Tile deletion completed.");
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public int insertTile(Tile tile) {
        Session session = DatabaseUtils.createSessionFactory().openSession();
        Transaction transaction = null;

        int tileID = 0;

        try {
            transaction = session.beginTransaction();
            tileID = (int) session.save(tile);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        // returning the id to be able to fetch it later on
        return tileID;
    }
}
