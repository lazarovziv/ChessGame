package com.zivlazarov.newengine;

public class ZMove {

    private int sourceRow;
    private int sourceCol;
    private int targetRow;
    private int targetCol;

    public ZMove(int sourceRow, int sourceCol, int targetRow, int targetCol) {
        this.sourceRow = sourceRow;
        this.sourceCol = sourceCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    public int getSourceRow() {
        return sourceRow;
    }

    public int getSourceCol() {
        return sourceCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    @Override
    public String toString() {
        return "[" + sourceRow + "," + sourceCol + "] -> " + "[" + targetRow + "," + targetCol + "]";
    }
}
