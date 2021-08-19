package com.zivlazarov.newengine;

import com.zivlazarov.newengine.ui.ZMoveLabel;

public class ZMove {

    private final int sourceRow;
    private final int sourceCol;
    private final int targetRow;
    private final int targetCol;

    private char capturedPiece = '-';

    private ZMoveLabel moveLabel = ZMoveLabel.REGULAR;

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

    public char getCapturedPiece() {
        return capturedPiece;
    }

    public ZMoveLabel getMoveLabel() {
        return moveLabel;
    }

    public void setCapturedPiece(char capturedPiece) {
        this.capturedPiece = capturedPiece;
    }

    public void setMoveLabel(ZMoveLabel moveLabel) {
        this.moveLabel = moveLabel;
    }

    @Override
    public String toString() {
        return "[" + (sourceRow + 1) + "," + (sourceCol + 1) + "] -> " + "[" + (targetRow + 1) + "," + (targetCol + 1) + "]";
    }
}
