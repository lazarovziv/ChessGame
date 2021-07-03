package com.zivlazarov.chessengine.model.move;

import java.util.Stack;

public class BackupMove {

    private final Stack<Move> backup;

    public BackupMove() {
        backup = new Stack<>();
    }

    public void addMove(Move move) {
        backup.push(move);
    }

    public void removeMove(Move move) {
        backup.remove(move);
    }

    public Move pop() {
        return backup.pop();
    }

    public Move peek() {
        return backup.peek();
    }

}
