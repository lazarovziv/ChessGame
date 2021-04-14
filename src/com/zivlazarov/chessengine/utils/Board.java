package com.zivlazarov.chessengine.utils;

import java.util.HashMap;
import java.util.Map;

public class Board {

    private Tile[][] board;

    private final Map<String, Piece> blackAlivePieces;
    private final Map<String, Piece> whiteAlivePieces;
    private GameSituation gameSituation;

    public Board() {
        board = new Tile[8][8];
        blackAlivePieces = new HashMap<>();
        whiteAlivePieces = new HashMap<>();

        TileColor[] colors = { TileColor.WHITE, TileColor.BLACK };

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                board[r][c] = new Tile(r, c, colors[(r+c) % colors.length]);
            }
        }
        gameSituation = GameSituation.NORMAL;
    }

    // thorough check on every tile to see if it's threatened by black and/or white, which pieces are alive etc.
    // TODO: check for "Check" and/or "Checkmate" and/or "Draw" situation
    public void checkBoard() {
        // call refresh() method after every turn instead
        for (Piece piece : whiteAlivePieces.values()) piece.refresh();
        for (Piece piece : blackAlivePieces.values()) piece.refresh();
/*
        if (blackAlivePieces.get('K').getIsInDanger()) {
            // TODO: add (perhaps new method for Piece interface?) if other pieces of same pieceColor can change the game situation
            if (blackAlivePieces.get('K').getTilesToMoveTo().size() == 0) gameSituation = GameSituation.CHECKMATE;
            else gameSituation = GameSituation.CHECK;
        }

        if (whiteAlivePieces.get('K').getIsInDanger()) {
            // TODO: add (perhaps new method for Piece interface?) if other pieces of same pieceColor can change the game situation
            if (whiteAlivePieces.get('K').getTilesToMoveTo().size() == 0) gameSituation = GameSituation.CHECKMATE;
            else gameSituation = GameSituation.CHECK;
        }
        */
    }

    public void printBoard() {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = 0; r < board.length; r++) {
            System.out.println();
            for (int c = 0; c < board.length; c++) {
                if (c == 0) /*System.out.print(letters[r] + " ");*/ System.out.print(nums[r] + " ");
                if (board[r][c].getPiece() != null) {
                    System.out.print(board[r][c].getPiece().getName() + " ");
                } else System.out.print("-- ");
            }
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            if (i == 0) System.out.print("  ");
            System.out.print(nums[i] + "  ");
        }
        System.out.println();
        System.out.println();
    }



    public Tile[][] getBoard() {
        return board;
    }

    public Map<String, Piece> getBlackAlivePieces() {
        return blackAlivePieces;
    }

    public Map<String, Piece> getWhiteAlivePieces() {
        return whiteAlivePieces;
    }

    public GameSituation getGameSituation() { return gameSituation; }
}
