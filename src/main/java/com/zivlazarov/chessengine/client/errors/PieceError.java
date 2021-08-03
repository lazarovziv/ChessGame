package com.zivlazarov.chessengine.client.errors;

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
