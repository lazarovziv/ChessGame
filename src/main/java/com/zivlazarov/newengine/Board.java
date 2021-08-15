package com.zivlazarov.newengine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board {

    public static int[] board;

    private static final int WHITE_PLAYER = 1;
    private static final int BLACK_PLAYER = -1;

    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    public static int[] whitePawnDirections = {9, 7};
    public static int[] blackPawnDirections = {-9, -7};
    public static int[] knightDirections = {15, 17, 10, 6, -15, -17, -10, -6};
    public static int[] bishopDirections = {9, 7, -9, -7};
    public static int[] rookDirections = {1, -1, 8, -8};
    public static int[] queenDirections = {1, -1, 8, -8, 9, 7, -9, -7};
    public static int[] kingDirections = {1, -1, 8, -8, 9, 7, -9, -7};

    public static boolean[] squaresThreatenedByBlack = new boolean[64];
    public static boolean[] squaresThreatenedByWhite = new boolean[64];

    public static final Map<Integer, Character> rows = Map.of(
            0, 'A',
            1, 'B',
            2, 'C',
            3, 'D',
            4, 'E',
            5, 'F',
            6, 'G',
            7, 'H'
    );

    public static final Map<Integer, String> whitePieces = Map.of(
            PAWN, "wP",
            KNIGHT, "wN",
            BISHOP, "wB",
            ROOK, "wR",
            QUEEN, "wQ",
            KING, "wK"
    );

    public static final Map<Integer, String> blackPieces = Map.of(
            -PAWN, "bP",
            -KNIGHT, "bN",
            -BISHOP, "bB",
            -ROOK, "bR",
            -QUEEN, "bQ",
            -KING, "bK"
    );

    public static void init() {
        board = new int[]
                {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK,
                PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN, PAWN,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN, -PAWN,
                -ROOK, -KNIGHT, -BISHOP, -QUEEN, -KING, -BISHOP, -KNIGHT, -ROOK};
    }

    public static void resetThreats() {
        for (int i = 0; i < board.length; i++) {
            squaresThreatenedByWhite[i] = false;
            squaresThreatenedByBlack[i] = false;
        }
    }

    public static List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();
        for (int squareIndex = 0; squareIndex < board.length; squareIndex++) {
            if (board[squareIndex] != 0) {
                int piece = board[squareIndex];
                switch (piece) {
                    case PAWN: moves.addAll(generatePawnMoves(squareIndex, WHITE_PLAYER));
                    case -PAWN: moves.addAll(generatePawnMoves(squareIndex, BLACK_PLAYER));
                    case KNIGHT: moves.addAll(generateKnightMoves(squareIndex, WHITE_PLAYER));
                    case -KNIGHT: moves.addAll(generateKnightMoves(squareIndex, BLACK_PLAYER));
                    case BISHOP: moves.addAll(generateSlidingPieceMoves(squareIndex, WHITE_PLAYER, bishopDirections));
                    case -BISHOP: moves.addAll(generateSlidingPieceMoves(squareIndex, BLACK_PLAYER, bishopDirections));
                    case ROOK: moves.addAll(generateSlidingPieceMoves(squareIndex, WHITE_PLAYER, rookDirections));
                    case -ROOK: moves.addAll(generateSlidingPieceMoves(squareIndex, BLACK_PLAYER, rookDirections));
                    case QUEEN: moves.addAll(generateSlidingPieceMoves(squareIndex, WHITE_PLAYER, queenDirections));
                    case -QUEEN: moves.addAll(generateSlidingPieceMoves(squareIndex, BLACK_PLAYER, queenDirections));
                    case KING: moves.addAll(generateKingMoves(squareIndex, WHITE_PLAYER));
                    case -KING: moves.addAll(generateKingMoves(squareIndex, BLACK_PLAYER));
                }
            }
        }
        return moves;
    }

    private static List<Move> generatePawnMoves(int squareIndex, int player) {
        List<Move> moves = new ArrayList<>();

        // double square move for pawn if still hasn't moved
        // 1 white -1 black
        if (player == WHITE_PLAYER) {
            // can move forward if square is empty
            if (!isOccupied(squareIndex + 8)) {
                Move move = new Move(squareIndex, squareIndex + 8);
                moves.add(move);
            }

            if (squareIndex >= 8 && squareIndex <= 15) {
                // if empty
                if (!isOccupied(squareIndex + 16)) {
                    Move move = new Move(squareIndex, squareIndex + 16);
                    moves.add(move);
                }
            }

            // eating directions
            for (int direction : whitePawnDirections) {
                if (!isValid(squareIndex + direction)) continue;
                // less than zero means occupied by opponent
                if (isOccupiedByBlack(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByWhite[squareIndex + direction] = true;
                }
            }

            // en passant
            if (squareIndex >= 32 && squareIndex <= 39) {
                for (int direction : whitePawnDirections) {
                    // needs to be a pawn for en passant
                    if (board[squareIndex + direction] == -PAWN) {
                        Move move = new Move(squareIndex, squareIndex + direction + 8);
                        moves.add(move);
                        squaresThreatenedByWhite[squareIndex + direction + 8] = true;
                    }
                }
            }

        } else {
            // can move forward if square is empty
            if (!isOccupied(squareIndex - 8)) {
                Move move = new Move(squareIndex, squareIndex - 8);
                moves.add(move);
            }

            if (squareIndex >= 48 && squareIndex <= 55) {
                // if empty
                if (!isOccupied(squareIndex - 16)) {
                    Move move = new Move(squareIndex, squareIndex - 16);
                    moves.add(move);
                }
            }

            // eating directions
            for (int direction : blackPawnDirections) {
                if (!isValid(squareIndex - direction)) continue;
                // bigger than zero means occupied by opponent (white)
                if (isOccupiedByWhite(squareIndex - direction)) {
                    Move move = new Move(squareIndex, squareIndex - direction);
                    moves.add(move);
                    squaresThreatenedByBlack[squareIndex + direction] = true;
                }
            }

            // en passant
            if (squareIndex >= 32 && squareIndex <= 39) {
                for (int direction : blackPawnDirections) {
                    // needs to be a pawn for en passant
                    if (board[squareIndex - direction] == PAWN) {
                        Move move = new Move(squareIndex, squareIndex - direction - 8);
                        moves.add(move);
                        squaresThreatenedByBlack[squareIndex - direction - 8] = true;
                    }
                }
            }
        }

        return moves;
    }

    private static List<Move> generateKnightMoves(int squareIndex, int player) {
        List<Move> moves = new ArrayList<>();

        for (int direction : knightDirections) {
            if (!isValid(squareIndex + direction)) continue;
            // white
            if (player == WHITE_PLAYER) {
                // occupied by black
                if (isOccupiedByBlack(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByWhite[squareIndex + direction] = true;
                } else if (!isOccupied(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByWhite[squareIndex + direction] = true;
                }
            } else {
                if (isOccupiedByWhite(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByBlack[squareIndex + direction] = true;
                } else if (!isOccupied(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByBlack[squareIndex + direction] = true;
                }
            }
        }
        return moves;
    }

    private static List<Move> generateSlidingPieceMoves(int squareIndex, int player, int[] directions) {
        List<Move> moves = new ArrayList<>();

        for (int direction : directions) {
            if (!isValid(squareIndex + direction)) continue;
            // incrementing direction
            for (int i = 1; i < board.length % 8; i++) {
                if (!isValid(squareIndex + direction*i)) break; // or continue (?)

                if (player == WHITE_PLAYER) {
                    // if occupied by opponent, add to moves and break (capture)
                    if (isOccupiedByBlack(squareIndex + direction*i)) {
                        Move move = new Move(squareIndex, squareIndex + direction*i);
                        moves.add(move);
                        squaresThreatenedByWhite[squareIndex + direction*i] = true;
                        break;
                        // if empty, add to moves and continue
                    } else if (!isOccupied(squareIndex + direction * i)) {
                        Move move = new Move(squareIndex, squareIndex + direction*i);
                        moves.add(move);
                        squaresThreatenedByWhite[squareIndex + direction*i] = true;
                        // if occupied by same piece color, break
                    } else if (isOccupiedByWhite(squareIndex + direction * i)) break;
                } else {
                    // if occupied by opponent, add to moves and break
                    if (isOccupiedByWhite(squareIndex + direction*i)) {
                        Move move = new Move(squareIndex, squareIndex + direction*i);
                        moves.add(move);
                        squaresThreatenedByBlack[squareIndex + direction*i] = true;
                        break;
                        // if empty, add to moves and continue
                    } else if (!isOccupied(squareIndex + direction * i)) {
                        Move move = new Move(squareIndex, squareIndex + direction*i);
                        moves.add(move);
                        squaresThreatenedByBlack[squareIndex + direction*i] = true;
                        // if occupied by same piece color, break
                    } else if (isOccupiedByBlack(squareIndex + direction * i)) break;
                }
            }
        }

        return moves;
    }

    private static List<Move> generateKingMoves(int squareIndex, int player) {
        List<Move> moves = new ArrayList<>();

        for (int direction : kingDirections) {
            if (!isValid(squareIndex + direction)) continue;
            // white
            if (player == WHITE_PLAYER) {
                // can't move to threatened square
                if (isSquareThreatenedByBlack(squareIndex + direction)) continue;
                // empty square
                if (!isOccupied(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByWhite[squareIndex + direction] = true;
                }
                // capturing
                if (isOccupiedByBlack(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByWhite[squareIndex + direction] = true;
                }
                // black
            } else {
                // can't move to threatened square
                if (isSquareThreatenedByWhite(squareIndex + direction)) continue;
                // empty square
                if (!isOccupied(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByBlack[squareIndex + direction] = true;
                }
                // capturing
                if (isOccupiedByWhite(squareIndex + direction)) {
                    Move move = new Move(squareIndex, squareIndex + direction);
                    moves.add(move);
                    squaresThreatenedByBlack[squareIndex + direction] = true;
                }
            }
        }

        return moves;
    }

    private static boolean isValid(int squareIndex) {
        if (squareIndex > board.length - 1) return false;
        return squareIndex >= 0;
    }

    private static boolean isOccupiedByWhite(int squareIndex) {
        return board[squareIndex] > 0;
    }

    private static boolean isOccupiedByBlack(int squareIndex) {
        return board[squareIndex] < 0;
    }

    private static boolean isOccupied(int squareIndex) {
        return board[squareIndex] != 0;
    }

    private static boolean isSquareThreatenedByBlack(int squareIndex) {
        return squaresThreatenedByBlack[squareIndex];
    }

    private static boolean isSquareThreatenedByWhite(int squareIndex) {
        return squaresThreatenedByWhite[squareIndex];
    }

    public static void printBoardWithLetters() {
        for (int i = 0; i < board.length; i++) {
            if (i % 8 == 0) System.out.println();
            if (board[i] > 0) System.out.print(whitePieces.get(board[i]) + " ");
            else if (board[i] < 0) System.out.print(blackPieces.get(board[i]) + " ");
            else System.out.print("-  ");
        }
    }

    public static void printBoard() {
        for (int i = 0; i < board.length; i++) {
            if (i % 8 == 0) System.out.println();
            if (board[i] != 0) {
                if (board[i] > 0)
                    System.out.print(board[i] + "  ");
                else System.out.print(board[i] + " ");
            }
            else System.out.print(" - ");
        }
    }

    public static String numberToString(int squareIndex) {
        int row = squareIndex % 8;
        int col = squareIndex / 8;

        return "" + rows.get(row) + (col+1);
    }
}