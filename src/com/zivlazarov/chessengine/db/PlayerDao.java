package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.player.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.UUID;

public class PlayerDao implements Dao {

    private static SessionFactory sessionFactory;
    private static Session session;

    public PlayerDao() {
        createConnection();
    }

    @Override
    public void createConnection() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Override
    public void connect() {
        session = sessionFactory.openSession();
    }

    @Override
    public void close() {
        session.close();
    }

    public Player findByID(UUID id) {
        connect();
        Transaction transaction = null;
        Player player = null;

        try {
            transaction = session.beginTransaction();

            player = session.get(Player.class, id);

            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return player;
    }

    public void insert(Player player) {
        connect();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            session.save(player);

            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void update(UUID id) {
        connect();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Player playerToUpdate = session.get(Player.class, id);
            session.update(playerToUpdate);

            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public void delete(UUID id) {
        connect();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Player player = session.get(Player.class, id);
            session.delete(player);

            transaction.commit();

        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
