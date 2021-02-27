package com.zivlazarov.chessengine;

import java.util.ArrayList;

public class Board {

    private Tile[][] board;

    private ArrayList<Piece> alivePieces;

    public Board() {
        board = new Tile[8][8];
        alivePieces = new ArrayList<Piece>();

        TileColor[] colors = { TileColor.WHITE, TileColor.BLACK };

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                board[r][c] = new Tile(r, c, colors[(r+c) % colors.length]);
            }
        }
    }

    // thorough check on every tile to see if it's threatened by black and/or white, which pieces are alive etc.
    // TODO: check for "Check" and/or "Checkmate" and/or "Draw" situation
    public void checkBoard() {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                Tile current = board[r][c];
                // checking threats on current tile in loop
                for (Piece piece : alivePieces) {
                    if (piece.getTilesToMoveTo().contains(current)) {
                        if (piece.getPieceColor() == PieceColor.WHITE) current.setThreatenedByWhite(true);
                        if (piece.getPieceColor() == PieceColor.BLACK) current.setThreatenedByBlack(true);
                    }
                }
            }
        }
    }

    void printBoard() {
        for (int r = 0; r < board.length; r++) {
            System.out.println();
            for (int c = 0; c < board.length; c++) {
                if (board[r][c].getPiece() != null) {
                    System.out.print(board[r][c].getPiece().getName() + " ");
                } else System.out.print("- ");
            }
        }
        System.out.println();
    }

    public Tile[][] getBoard() {
        return board;
    }

    public ArrayList<Piece> getAlivePieces() {
        return alivePieces;
    }
}
