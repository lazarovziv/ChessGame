package com.zivlazarov.chessengine.model.utils;

public interface MyObservable {

    void updateObservers();
    void updateObserver(MyObserver observer);

    void addObserver(MyObserver observer);
    void addAllObservers(MyObserver[] observers);
    void removeObserver(MyObserver observer);
    void setChanged();
    boolean hasChanged();
    void clearChanged();
}
