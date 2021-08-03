package com.zivlazarov.chessengine.client.model.utils;

public class Memento<T> {

    private T state;

    public Memento(T stateToSave) {
        state = stateToSave;
    }

    public T getSavedState() {
        return state;
    }
}
