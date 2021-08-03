package com.zivlazarov.chessengine.client.model.utils;

public interface MyObserver {

    void update();

    void setObservable(MyObservable observable);
}
