package com.zivlazarov.chessengine.model.utils;

public interface Observable {

    void updateAll();
    void attach();
    void detach();
}
