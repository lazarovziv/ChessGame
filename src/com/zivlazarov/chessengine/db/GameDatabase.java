package com.zivlazarov.chessengine.db;

public class GameDatabase {

    private static GameDatabase instance;

    private static PlayerDao playerDao;

    private GameDatabase() {
        playerDao = new PlayerDao();
    }

    public static GameDatabase getInstance() {
        if (instance == null) {
            synchronized (GameDatabase.class) {
                if (instance == null) {
                    instance = new GameDatabase();
                }
            }
        }
        return instance;
    }

    public PlayerDao playerDao() {
        return playerDao;
    }
}
