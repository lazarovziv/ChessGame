package com.zivlazarov.chessengine.model.utils;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.Piece;

import java.io.Serializable;

public class Pair<T, V> implements Serializable {

    private T first;
    private V second;

    public Pair(T first, V second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public String toString() {
        if (first instanceof Tile && second instanceof Tile) {
            return first.toString() + " -> " + second.toString();
        }

        if (first != null && second != null) {
            return first.toString() + " - " + second.toString();
        } else if (first != null && second == null) {
            return first.toString();
        } else if (second != null && first == null) {
            return second.toString();
        } else return "!!!";
    }

    public boolean equals(Pair pair) {
        return pair.getFirst().equals(first) && pair.getSecond().equals(second);
    }
//    public boolean equals(Pair<Piece, Tile> pair) {
//        return pair.getFirst().equals(first) && pair.getSecond().equals(second);
//    }
}
