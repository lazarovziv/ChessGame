package com.zivlazarov.chessengine.model.utils;

public interface MyObserver {

    void update();

    void setObservable(MyObservable observable);
}
