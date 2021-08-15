package com.zivlazarov.chessengine.model.board;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class ZBoard {

    public static final int NUM_OF_PIECES = 12;
    public static final int NUM_OF_SQUARES = 64;

    public static final long[][] zBoard = new long[NUM_OF_PIECES][NUM_OF_SQUARES];
    // WK, WQ, BK, BQ
    public static final long[] zCastles = new long[4];
    // one en passant move for each file (column) in board
    public static final long[] zEnPassants = new long[8];

    public static final Map<Long, long[][]> transpositionTable = new HashMap<>();
    public static final Map<Integer, Character> pieceTypes = new HashMap<>();
    public static final Map<Character, Long> pieceTypesReversed = new HashMap<>();

    // initialized board
    public static long[] board = new long[64];

    static int[] whitePawnsInitSquares = {8,9,10,11,12,13,14,15};
    static int[] blackPawnsInitSquares = {48,49,50,51,52,53,54,55};
    static int[] whiteKnightsInitSquares = {1, 6};
    static int[] blackKnightsInitSquares = {57, 62};
    static int[] whiteBishopsInitSquares = {2, 5};
    static int[] blackBishopsInitSquares = {58, 61};
    static int[] whiteRooksInitSquares = {0, 7};
    static int[] blackRooksInitSquares = {56, 63};
    static int[] whiteQueenInitSquare = {3};
    static int[] blackQueenInitSquare = {59};
    static int[] whiteKingInitSquare = {4};
    static int[] blackKingInitSquare = {60};

    // initializing the zobrist hash to 0 at start of game
    public static long zobristHash = 0L;

    // if it's black's turn, XOR this value after generating the zHash
    public static long blackToMove;

    public static void initZBoard() {
        pieceTypes.put(0, '0');
        pieceTypes.put(1, 'P');
        pieceTypes.put(2, 'N');
        pieceTypes.put(3, 'B');
        pieceTypes.put(4, 'R');
        pieceTypes.put(5, 'Q');
        pieceTypes.put(6, 'K');
        pieceTypes.put(7, 'p');
        pieceTypes.put(8, 'n');
        pieceTypes.put(9, 'b');
        pieceTypes.put(10, 'r');
        pieceTypes.put(11, 'q');
        pieceTypes.put(12, 'k');

        for (int i = 0; i < pieceTypes.size(); i++) {
            pieceTypesReversed.put(pieceTypes.get(i), (long) i);
        }

        blackToMove = generateRandom64BitLong();

        for (int piece = 0; piece < NUM_OF_PIECES; piece++) {
            // initializing each piece's bitboard (1 is occupied 0 is empty)
            switch (piece) {
                // white pawn
                case 0 -> {
                    for (int i = 0; i < whitePawnsInitSquares.length; i++) {
                        zBoard[piece][whitePawnsInitSquares[i]] = 1;
                        board[whitePawnsInitSquares[i]] = 1;
                    }
                }
                // black pawn
                case 1 -> {
                    for (int i = 0; i < blackPawnsInitSquares.length; i++) {
                        zBoard[piece][blackPawnsInitSquares[i]] = 1;
                        board[blackPawnsInitSquares[i]] = 7;
                    }
                }
                // white knight
                case 2 -> {
                    for (int i = 0; i < whiteKnightsInitSquares.length; i++) {
                        zBoard[piece][whiteKnightsInitSquares[i]] = 1;
                        board[whiteKnightsInitSquares[i]] = 2;
                    }
                }
                // black knight
                case 3 -> {
                    for (int i = 0; i < blackKnightsInitSquares.length; i++) {
                        zBoard[piece][blackKnightsInitSquares[i]] = 1;
                        board[blackKnightsInitSquares[i]] = 8;
                    }
                }
                // white bishop
                case 4 -> {
                    for (int i = 0; i < whiteBishopsInitSquares.length; i++) {
                        zBoard[piece][whiteBishopsInitSquares[i]] = 1;
                        board[whiteBishopsInitSquares[i]] = 3;
                    }
                }
                // black bishop
                case 5 -> {
                    for (int i = 0; i < blackBishopsInitSquares.length; i++) {
                        zBoard[piece][blackBishopsInitSquares[i]] = 1;
                        board[blackBishopsInitSquares[i]] = 9;
                    }
                }
                // white rook
                case 6 -> {
                    for (int i = 0; i < whiteRooksInitSquares.length; i++) {
                        zBoard[piece][whiteRooksInitSquares[i]] = 1;
                        board[whiteRooksInitSquares[i]] = 4;
                    }
                }
                // black rook
                case 7 -> {
                    for (int i = 0; i < blackRooksInitSquares.length; i++) {
                        zBoard[piece][blackRooksInitSquares[i]] = 1;
                        board[blackRooksInitSquares[i]] = 10;
                    }
                }
                // white queen
                case 8 -> {
                    for (int i = 0; i < whiteQueenInitSquare.length; i++) {
                        zBoard[piece][whiteQueenInitSquare[i]] = 1;
                        board[whiteQueenInitSquare[i]] = 5;
                    }
                }
                // black queen
                case 9 -> {
                    for (int i = 0; i < blackQueenInitSquare.length; i++) {
                        zBoard[piece][blackQueenInitSquare[i]] = 1;
                        board[blackQueenInitSquare[i]] = 11;
                    }
                }
                // white king
                case 10 -> {
                    for (int i = 0; i < whiteKingInitSquare.length; i++) {
                        zBoard[piece][whiteKingInitSquare[i]] = 1;
                        board[whiteKingInitSquare[i]] = 6;
                    }
                }
                // black king
                case 11 -> {
                    for (int i = 0; i < blackKingInitSquare.length; i++) {
                        zBoard[piece][blackKingInitSquare[i]] = 1;
                        board[blackKingInitSquare[i]] = 12;
                    }
                }
            }
        }

        // setting random values for occupied squares in every board
        for (int piece = 0; piece < NUM_OF_PIECES; piece++) {
            for (int square = 0; square < NUM_OF_SQUARES; square++) {
                if (zBoard[piece][square] != 0) {
                    zBoard[piece][square] = generateRandom64BitLong();
                    // XORing the zobrist hash only with occupied squares in the board
                    // XORing zobrist hash with current piece
                    zobristHash ^= zBoard[piece][square];
                }
            }
        }

        for (int i = 0; i < zEnPassants.length; i++) {
            if (i < 4) {
                zCastles[i] = generateRandom64BitLong();
                zobristHash ^= zCastles[i];
            }
            zEnPassants[i] = generateRandom64BitLong();
            zobristHash ^= zEnPassants[i];
        }

        System.out.println("Zobrist hash: " + zobristHash);

        // caching the board in the transposition table
        transpositionTable.put(zobristHash, zBoard);
    }

    public static long calculateZobristHash() {
        // setting random values for occupied squares in every board
        for (int piece = 0; piece < NUM_OF_PIECES; piece++) {
            for (int square = 0; square < NUM_OF_SQUARES; square++) {
                if (zBoard[piece][square] != 0) {
                    // XORing the zobrist hash only with occupied squares in the board
                    zobristHash ^= zBoard[piece][square];
                }
            }
        }
        return zobristHash;
    }

    public static void move(long sourceSquare, long targetSquare) {

    }

    // example of FEN: rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
    public long[] importFromFEN(String fen) {
        long[] board = new long[64];
        boolean castleWK = false;
        boolean castleWQ = false;
        boolean castleBK = false;
        boolean castleBQ = false;

        String[] fenSections = fen.split(" ");

        int charIndex = 0;

        String[] rows = fenSections[0].split("/");
        for (String row : rows) {
            for (char c : row.toCharArray()) {
                switch (c) {
                    case 'P': board[charIndex] = pieceTypesReversed.get('P');
                    case 'p': board[charIndex] = pieceTypesReversed.get('p');
                    case 'N': board[charIndex] = pieceTypesReversed.get('N');
                    case 'n': board[charIndex] = pieceTypesReversed.get('n');
                    case 'B': board[charIndex] = pieceTypesReversed.get('B');
                    case 'b': board[charIndex] = pieceTypesReversed.get('b');
                    case 'R': board[charIndex] = pieceTypesReversed.get('R');
                    case 'r': board[charIndex] = pieceTypesReversed.get('r');
                    case 'Q': board[charIndex] = pieceTypesReversed.get('Q');
                    case 'q': board[charIndex] = pieceTypesReversed.get('q');
                    case 'K': board[charIndex] = pieceTypesReversed.get('K');
                    case 'k': board[charIndex] = pieceTypesReversed.get('k');

                }
            }
            charIndex += row.length();
        }

        // current turn
        char currentTurn = fenSections[1].charAt(0);
        if (currentTurn == 'b') {
            blackToMove = generateRandom64BitLong();
            zobristHash ^= blackToMove;
        }

        // castling
        for (char c : fenSections[2].toCharArray()) {
            if (c != '-') {
                switch (c) {
                    case 'K': castleWK = true;
                    case 'Q': castleWQ = true;
                    case 'k': castleBK = true;
                    case 'q': castleBQ = true;
                }
            }
        }



        return board;
    }

    public static long generateRandom64BitLong() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextLong();
    }
}
