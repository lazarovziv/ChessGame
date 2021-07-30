package com.zivlazarov.chessengine.db;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class DatabaseUtils {

    public static volatile SessionFactory sessionFactory = null;

    public static final String DB_URL = "jdbc:mysql://localhost/chess";
    public static final String USER = "zivlazarov";
    public static final String PASS = "zivlazarov";

    public static SessionFactory createSessionFactory() {
        try {
            if (sessionFactory == null) {
                synchronized (SessionFactory.class) {
                    if (sessionFactory == null) {
                        sessionFactory = new Configuration()
                                .configure(new File("hibernate.cfg.xml"))
                                .addAnnotatedClass(Player.class)
                                .addAnnotatedClass(Tile.class)
                                .addAnnotatedClass(Piece.class)
                                .addAnnotatedClass(Move.class)
                                .buildSessionFactory();
                        return sessionFactory;
                    }
                }
            }
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        sessionFactory.close();
    }
}
