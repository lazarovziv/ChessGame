package com.zivlazarov.chessengine.model.utils;

import java.io.Serializable;

public interface MyObservable extends Serializable {

    void updateObservers();
    void updateObserver(MyObserver observer);

    void addObserver(MyObserver observer);
    void addAllObservers(MyObserver[] observers);
    void removeObserver(MyObserver observer);
    void setChanged();
    boolean hasChanged();
    void clearChanged();
}
