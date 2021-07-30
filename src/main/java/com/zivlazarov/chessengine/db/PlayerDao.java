package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.player.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.io.File;
import java.sql.*;
import java.util.List;

import static com.zivlazarov.chessengine.db.DatabaseUtils.*;

public class PlayerDao implements Dao {

    /*
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("chess");

    public void insertPlayer(Player player) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

            manager.persist(player);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public Player findPlayerByID(int id) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        // playerID is a parameterized query
        String query = "SELECT p FROM player p WHERE p.id =:playerID";

        TypedQuery<Player> typedQuery = manager.createQuery(query, Player.class);
        typedQuery.setParameter("playerID", id);

        Player player = null;
        try {
            player = typedQuery.getSingleResult();
            System.out.println(player.getName());
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            manager.close();
        }
        return player;
    }

    public List<Player> findAllPlayers() {
        EntityManager manager = entityManagerFactory.createEntityManager();
        String query = "SELECT p FROM player p WHERE p.id IS NOT NULL";

        TypedQuery<Player> typedQuery = manager.createQuery(query, Player.class);

        List<Player> players = null;

        try {
            players = typedQuery.getResultList();
            players.forEach(System.out::println);
        } catch (NoResultException e) {
            e.printStackTrace();
        } finally {
            manager.close();
        }
        return players;
    }

    public void updatePlayer(Player player) {
        EntityManager manager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = null;

        try {
            transaction = manager.getTransaction();
            transaction.begin();

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            manager.close();
        }
    }

    public void insertPlayer(Player player) {
        Session session = DatabaseUtils.createSessionFactory().openSession();
        session.beginTransaction();

        player.saveState();
        Player playerToSave = player.loadState();

        session.save(playerToSave);

        session.getTransaction().commit();
    }

    public Player findPlayerByID(int id) {
        Session session = DatabaseUtils.createSessionFactory().openSession();
        session.beginTransaction();

        Player player = (Player) session.get(Player.class, id);

        session.getTransaction().commit();

        return player;
    } */

//    public void insertPlayer(Player player) {
//        Connection connection = null;
//        Statement statement = null;
//
//        try {
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            connection = DriverManager.getConnection(DB_URL, USER, PASS);
//            statement = connection.createStatement();
//
//            String query = "INSERT INTO player(id, is_AI, is_current_player, name, player_direction, player_score, player_color) " +
//                    "VALUES (" + player.getId() + ", " +
//                    player.isAI() + ", " +
//                    player.isCurrentPlayer() + ", " +
//                    player.getName() + ", " +
//                    player.getPlayerDirection() + ", " +
//                    player.getPlayerScore() + ", " +
//                    player.getPlayerColor() + ");";
//
//            statement.executeUpdate(query);
//
//            System.out.println("Insertion complete.");
//
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }

    public int insertPlayer(Player player) {
        Session session = DatabaseUtils.createSessionFactory().openSession();
        Transaction transaction = null;

        int playerID = 0;

        try {
            transaction = session.beginTransaction();
            playerID = (int) session.save(player);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }

        // returning the id to be able to fetch it later on
        return playerID;
    }
}
