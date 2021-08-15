package com.zivlazarov.chessengine.model.move;

public class ZMove {

    private int sourceSquare;
    private int targetSquare;

    public ZMove(int sourceSquare, int targetSquare) {
        this.sourceSquare = sourceSquare;
        this.targetSquare = targetSquare;
    }

    public int getSourceSquare() {
        return sourceSquare;
    }

    public int getTargetSquare() {
        return targetSquare;
    }

    @Override
    public String toString() {
        return sourceSquare + " -> " + targetSquare;
    }
}
