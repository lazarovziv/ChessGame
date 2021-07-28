package com.zivlazarov.chessengine.db;

public interface Dao {

    void createConnection();
    void connect();
    void close();
}
