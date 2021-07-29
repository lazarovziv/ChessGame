package com.zivlazarov.chessengine.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class DatabaseUtils {

    public static SessionFactory sessionFactory = null;

    public static SessionFactory createSessionFactory() {
        try {
            if (sessionFactory == null) {
                synchronized (SessionFactory.class) {
                    if (sessionFactory == null) {
                        sessionFactory = new Configuration().configure().buildSessionFactory();
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
