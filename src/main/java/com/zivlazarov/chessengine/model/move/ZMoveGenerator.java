package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.ZBoard;

import java.util.ArrayList;
import java.util.List;

public class ZMoveGenerator {

    private static int[] pawnsDirections = {8, 7, 9};
    private static int[] kingsDirections = {7, 8, 9, -1, 1, -9, -8, -7};
    private static int[] bishopDirections = {7, 9, -9, -7};
    private static int[] rookDirections = {8, -8, 1, -1};
    private static int[] queenDirections = {7, 8, 9, -1, 1, -9, -8, -7};
    private static int[] knightDirections = {17, 15, -17, -15, 10, -6, -10, 6};

    public static List<ZMove> generateMoves() {
        List<ZMove> moves = new ArrayList<>();
        for (int piece = 0; piece < ZBoard.NUM_OF_PIECES; piece++) {
            for (int square = 0; square < ZBoard.NUM_OF_SQUARES; square++) {
                if (ZBoard.zBoard[piece][square] != 0) {
                    switch (piece) {
                        // white pawn
                        case 0 -> {
                            moves.addAll(generatePawnsMove(square, 1));
                        }
                        // black pawn
                        case 1 -> {
                            moves.addAll(generatePawnsMove(square, -1));
                        }
                        // white knight
                        case 2 -> {

                        }
                        // black knight
                        case 3 -> {

                        }
                        // white bishop
                        case 4 -> {

                        }
                        // black bishop
                        case 5 -> {

                        }
                        // white rook
                        case 6 -> {

                        }
                        // black rook
                        case 7 -> {

                        }
                        // white queen
                        case 8 -> {

                        }
                        // black queen
                        case 9 -> {

                        }
                        // white king
                        case 10 -> {

                        }
                        // black king
                        case 11 -> {

                        }
                    }
                }
            }
        }
        return moves;
    }

    private static List<ZMove> generatePawnsMove(int square, int player) {
        List<ZMove> moves = new ArrayList<>();
        // checking if moved
        if (player == 1) { // white
            if (square >= 8 && square <= 15) {
                if (ZBoard.zBoard[0][square + 8] == 0) {
                    ZMove move = new ZMove(square, square + 8);
                    moves.add(move);
                }
            }
        } else { // black
            if (square >= 47 && square <= 54) {
                if (ZBoard.zBoard[1][square - 8] == 0) {
                    ZMove move = new ZMove(square, square - 8);
                    moves.add(move);
                }
            }
        }

        int p = player == 1 ? 6 : 0;
        boolean atPossibleEnPassantSquare = player == 1 ? square >= 24 && square <= 31 : square >= 32 && square <= 39;
        // iterating on opponent's boards
        for (; p < ZBoard.NUM_OF_PIECES; p++) {
            for (int direction : pawnsDirections) {
                if (ZBoard.zBoard[p][square + player * direction] != 0) {
                    ZMove move = new ZMove(square, square + player * direction);
                    moves.add(move);
                }
                // en passant
                if (atPossibleEnPassantSquare) {
                    if (ZBoard.zBoard[p][square + player] != 0) {
                        ZMove move = new ZMove(square, square - player * 7);
                        moves.add(move);
                    }
                    if (ZBoard.zBoard[p][square - player] != 0) {
                        ZMove move = new ZMove(square, square - player * 9);
                        moves.add(move);
                    }
                }
            }
        }
        return moves;
    }
}
