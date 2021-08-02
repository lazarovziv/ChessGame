package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.player.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.UUID;

import static com.zivlazarov.chessengine.db.DatabaseUtils.*;

public class PlayerDao implements Dao {

    public long insertPlayer(Player player) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        long id = player.getId();
        boolean isAI = player.isAI();
        boolean isCurrentPlayer = player.isCurrentPlayer();
        String name = player.getName();
        int playerDirection = player.getPlayerDirection();
        int playerScore = player.getPlayerScore();
        PieceColor playerColor = player.getPlayerColor();
        int color;
        if (playerColor == PieceColor.WHITE) color = 0;
        else color = 1;

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "INSERT INTO Player(id, isAI, isCurrentPlayer, name, playerDirection, playerScore, playerColor) " +
                    "VALUES (" + id + ", " +
                    isAI + ", " +
                    isCurrentPlayer + ", " +
                    name + ", " +
                    playerDirection + ", " +
                    playerScore + ", " +
                    color  + ");";

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

    public long deletePlayer(Player player) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        long id = player.getId();

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            statement = connection.createStatement();

            String query = "DELETE FROM Player WHERE id = " + player.getId();

            statement.executeUpdate(query);

            System.out.println("Deletion complete.");

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

    public Player findPlayerByID(long id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        Player player = new Player();

        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            String query = "SELECT * FROM Player WHERE id = " + id;

            preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                player.setId(id);
                player.setAI(resultSet.getBoolean(1));
                player.setIsCurrentPlayer(resultSet.getBoolean(2));
                player.setName(resultSet.getString(3));
                player.setPlayerDirection(resultSet.getInt(4));
                player.setPlayerScore(resultSet.getInt(5));
                PieceColor playerColor = resultSet.getInt(6) == 0 ? PieceColor.WHITE : PieceColor.BLACK;
                player.setPlayerColor(playerColor);
            }

            System.out.println("Retrieve of player is complete.");

        } catch (SQLException /*| ClassNotFoundException*/ e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connection.close();
        }
        return player;
    }

}
