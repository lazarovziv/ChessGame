package com.zivlazarov.chessengine.errors;

public class IllegalMoveError extends Error {

    private String message;

    public IllegalMoveError(String message) {
        super(message);
        this.message = message;
    }

    public IllegalMoveError() {
        super("Illegal Move!");
    }

    public String getMessage() {
        return message;
    }
}
