package com.zivlazarov.newengine;

public class Move {

    private int sourceSquare;
    private int targetSquare;

    public Move(int sourceSquare, int targetSquare) {
        this.sourceSquare = sourceSquare;
        this.targetSquare = targetSquare;
    }

    public int getSourceSquare() {
        return sourceSquare;
    }

    public void setSourceSquare(int sourceSquare) {
        this.sourceSquare = sourceSquare;
    }

    public int getTargetSquare() {
        return targetSquare;
    }

    public void setTargetSquare(int targetSquare) {
        this.targetSquare = targetSquare;
    }

    @Override
    public String toString() {
        return sourceSquare + " -> " + targetSquare;
    }
}
