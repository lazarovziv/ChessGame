package com.zivlazarov.chessengine.model.utils;

public class Memento<T> {

    private final T state;

    public Memento(T stateToSave) {
        state = stateToSave;
    }

    public T getSavedState() {
        return state;
    }
}
