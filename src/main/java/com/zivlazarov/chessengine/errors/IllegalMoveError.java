package com.zivlazarov.chessengine.errors;

public class IllegalMoveError extends Error {

    private String message;

    public IllegalMoveError(String message) {
        super(message);
        this.message = message;
        super.printStackTrace();
    }

    public IllegalMoveError() {
        super("Illegal Move!");
        super.printStackTrace();
    }

    public String getMessage() {
        return message;
    }
}
