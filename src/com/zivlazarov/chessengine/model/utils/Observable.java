package com.zivlazarov.chessengine.model.utils;

public interface Observable {

    void attach(Observer observer);
    void detach(Observer observer);
    void notifyAll();
}