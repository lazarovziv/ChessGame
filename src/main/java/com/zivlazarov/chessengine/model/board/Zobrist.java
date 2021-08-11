package com.zivlazarov.chessengine.model.board;

import java.security.SecureRandom;

public class Zobrist {

    public static final int NUM_OF_PLAYERS = 2;
    public static final int NUM_OF_PIECES = 6;
    public static final int NUM_OF_TILES = 64;

    public static final long[][][] zobristArray = new long[NUM_OF_PLAYERS][NUM_OF_PIECES][NUM_OF_TILES];
    private static final long[] zobristEnPassant = new long[8];
    private static final long[] zobristCastling = new long[4];
    private static long zobristBlackMove;


    public static long random64() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextLong();
    }

    public static void zobristFillArray() {
        for (int player = 0; player < NUM_OF_PLAYERS; player++) {
            for (int piece = 0; piece < NUM_OF_PIECES; piece++) {
                for (int tile = 0; tile < NUM_OF_TILES; tile++) {
                    zobristArray[player][piece][tile] = random64();
                }
            }
        }
        for (int i = 0; i < zobristEnPassant.length; i++) {
            zobristEnPassant[i] = random64();
        }
        for (int i = 0; i < zobristCastling.length; i++) {
            zobristCastling[i] = random64();
        }
        zobristBlackMove = random64();
    }

    public static long zobristHash(long whitePawn, long whiteKnight, long whiteBishop, long whiteRook, long whiteQueen, long whiteKing,
                                   long blackPawn, long blackKnight, long blackBishop, long blackRook, long blackQueen, long blackKing) {
        long zobristKey = 0L;

        for (int tile = 0; tile < NUM_OF_TILES; tile++) {
            if (((whitePawn >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackPawn >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            } else if (((whiteKnight >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackKnight >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            } else if (((whiteBishop >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackBishop >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            } else if (((whiteRook >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackRook >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            } else if (((whiteQueen >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackQueen >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            } else if (((whiteKing >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[0][0][tile];
            } else if (((blackKing >> tile) & 1) == 1) {
                zobristKey ^= zobristArray[1][0][tile];
            }
        }

        return zobristKey;
    }
}
