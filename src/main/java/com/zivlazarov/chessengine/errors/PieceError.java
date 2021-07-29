package com.zivlazarov.chessengine.errors;

public class PieceError extends Error {

    private String message;

    public PieceError(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
