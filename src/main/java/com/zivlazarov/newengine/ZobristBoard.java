package com.zivlazarov.newengine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;

public class ZobristBoard {

    private long zobristHash = 0L;
    private final long[][][] board;
    private char[][] displayBoard;

    private long blackToMove;
    private final long[] enPassant; // size of 8
    private final long[] castling; // size of 4

    private final SecureRandom random;

    public static final Map<Long, long[][][]> transpositionTable = new HashMap<>();

    private final List<ZMove> moves;
    private int currentPlayer = 0;

    private final Map<Character, Integer> piecesTypeValuesMap = new HashMap<>();

    private final Map<Integer, Integer> pawnsStartRow = Map.of(
            WHITE_PLAYER, 1,
            BLACK_PLAYER, 6
    );

    private final Map<Integer, Integer> enPassantRows = Map.of(
            WHITE_PLAYER, 4,
            BLACK_PLAYER, 3
    );

    private final int[][] bishopDirections = new int[][] {
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    private final int[][] rookDirections = new int[][] {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1}
    };

    private final int[][] queenDirections = new int[][] {
            {1, 0},
            {-1, 0},
            {0, 1},
            {0, -1},
            {1, 1},
            {1, -1},
            {-1, 1},
            {-1, -1}
    };

    private final boolean[][] whiteThreateningTiles = new boolean[8][8];
    private final boolean[][] blackThreateningTiles = new boolean[8][8];

//    // first indexes are white's pieces
    private final int[][] wPawnsLocations = new int[8][8];
    private final int[][] wKnightsLocations = new int[2][2];
    private final int[][] wBishopsLocations = new int[2][2];;
    private final int[][] wRooksLocations = new int[2][2];;
    private final int[][] wQueenLocation = new int[1][2];;
    private final int[][] wKingLocation = new int[1][2];;

    private final int[][] bPawnsLocations = new int[8][8];
    private final int[][] bKnightsLocations = new int[2][2];
    private final int[][] bBishopsLocations = new int[2][2];;
    private final int[][] bRooksLocations = new int[2][2];;
    private final int[][] bQueenLocation = new int[1][2];;
    private final int[][] bKingLocation = new int[1][2];;

    private static final int WHITE_PLAYER = 1;
    private static final int BLACK_PLAYER = -1;

    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;

    public static final Map<Character, String> piecesImagesMap = new HashMap<>();

    public ZobristBoard() {
        board = new long[12][8][8]; // 12 types of pieces (2 players * 6 types) and 8 rows of * 8 tiles
        displayBoard = new char[8][8];
        enPassant = new long[8];
        castling = new long[4];
        moves = new ArrayList<>();

        random = new SecureRandom();

        piecesTypeValuesMap.put('P', 0);
        piecesTypeValuesMap.put('N', 1);
        piecesTypeValuesMap.put('B', 2);
        piecesTypeValuesMap.put('R', 3);
        piecesTypeValuesMap.put('Q', 4);
        piecesTypeValuesMap.put('K', 5);
        piecesTypeValuesMap.put('p', 6);
        piecesTypeValuesMap.put('n', 7);
        piecesTypeValuesMap.put('b', 8);
        piecesTypeValuesMap.put('r', 9);
        piecesTypeValuesMap.put('q', 10);
        piecesTypeValuesMap.put('k', 11);

        piecesImagesMap.put('P', currentPath + "/src/main/java/" + "whitePawn.png");
        piecesImagesMap.put('N', currentPath + "/src/main/java/" + "whiteKnight.png");
        piecesImagesMap.put('B', currentPath + "/src/main/java/" + "whiteBishop.png");
        piecesImagesMap.put('R', currentPath + "/src/main/java/" + "whiteRook.png");
        piecesImagesMap.put('Q', currentPath + "/src/main/java/" + "whiteQueen.png");
        piecesImagesMap.put('K', currentPath + "/src/main/java/" + "whiteKing.png");
        piecesImagesMap.put('p', currentPath + "/src/main/java/" + "blackPawn.png");
        piecesImagesMap.put('n', currentPath + "/src/main/java/" + "blackKnight.png");
        piecesImagesMap.put('b', currentPath + "/src/main/java/" + "blackBishop.png");
        piecesImagesMap.put('r', currentPath + "/src/main/java/" + "blackRook.png");
        piecesImagesMap.put('q', currentPath + "/src/main/java/" + "blackQueen.png");
        piecesImagesMap.put('k', currentPath + "/src/main/java/" + "blackKing.png");

        displayBoard = new char[][] {
                {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'},
                {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-'},
                {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},
                {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'}};

        for (int pieceType = 0; pieceType < 12; pieceType++) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    board[pieceType][row][col] = random.nextLong();
                }
            }
        }

        blackToMove = random.nextLong();

        int wPawnsIndex = 0;
        int wKnightsIndex = 0;
        int wBishopsIndex = 0;
        int wRooksIndex = 0;

        int bPawnsIndex = 0;
        int bKnightsIndex = 0;
        int bBishopsIndex = 0;
        int bRooksIndex = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!isEmpty(row, col)) {
                    switch (displayBoard[row][col]) {
                        case 'P' -> {
                            wPawnsLocations[wPawnsIndex][0] = row;
                            wPawnsLocations[wPawnsIndex][1] = col;
                            wPawnsIndex++;
                        }
                        case 'N' -> {
                            wKnightsLocations[wKnightsIndex][0] = row;
                            wKnightsLocations[wKnightsIndex][1] = col;
                            wKnightsIndex++;
                        }
                        case 'B' -> {
                            wBishopsLocations[wBishopsIndex][0] = row;
                            wBishopsLocations[wBishopsIndex][1] = col;
                            wBishopsIndex++;
                        }
                        case 'R' -> {
                            wRooksLocations[wRooksIndex][0] = row;
                            wRooksLocations[wRooksIndex][1] = col;
                            wRooksIndex++;
                        }
                        case 'Q' -> {
                            wQueenLocation[0][0] = row;
                            wQueenLocation[0][1] = col;
                        }
                        case 'K' -> {
                            wKingLocation[0][0] = row;
                            wKingLocation[0][1] = col;
                        }
                        case 'p' -> {
                            bPawnsLocations[bPawnsIndex][0] = row;
                            bPawnsLocations[bPawnsIndex][1] = col;
                            bPawnsIndex++;
                        }
                        case 'n' -> {
                            bKnightsLocations[bKnightsIndex][0] = row;
                            bKnightsLocations[bKnightsIndex][1] = col;
                            bKnightsIndex++;
                        }
                        case 'b' -> {
                            bBishopsLocations[bBishopsIndex][0] = row;
                            bBishopsLocations[bBishopsIndex][1] = col;
                            bBishopsIndex++;
                        }
                        case 'r' -> {
                            bRooksLocations[bRooksIndex][0] = row;
                            bRooksLocations[bRooksIndex][1] = col;
                            bRooksIndex++;
                        }
                        case 'q' -> {
                            bQueenLocation[0][0] = row;
                            bQueenLocation[0][1] = col;
                        }
                        case 'k' -> {
                            bKingLocation[0][0] = row;
                            bKingLocation[0][1] = col;
                        }
                    }
                }
            }
        }

        currentPlayer = WHITE_PLAYER;

        calculateZobristHash();
        transpositionTable.put(zobristHash, board);

        System.out.println(zobristHash + "\n");
    }

    public long makeMove(ZMove move) {
        if (!moves.contains(move)) return zobristHash;

        char piece = displayBoard[move.getSourceRow()][move.getSourceCol()];
        int pieceBoardIndex = piecesTypeValuesMap.get(piece);

        // regular move
        // setting tile empty
        displayBoard[move.getSourceRow()][move.getSourceCol()] = '-';
        zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];

        // TODO: add capture, en passant, castling, pawn promotion
        if (isEmpty(move.getTargetRow(), move.getTargetCol())) {
            // moving the piece
            displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
            zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
        }

        transpositionTable.put(zobristHash, board);

        // changing turns
        currentPlayer *= -1;

        if (currentPlayer == BLACK_PLAYER) {
            zobristHash ^= blackToMove;
        }

        return zobristHash;
    }

    public long unmakeMove(ZMove move) {
        char piece = displayBoard[move.getTargetRow()][move.getTargetCol()];
        int pieceBoardIndex = piecesTypeValuesMap.get(piece);

        // setting tile empty
        displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
        zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];

        // moving the piece
        displayBoard[move.getSourceRow()][move.getSourceCol()] = piece;
        zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];

        // changing back the turn
        currentPlayer *= -1;

        if (currentPlayer == WHITE_PLAYER) {
            zobristHash ^= blackToMove;
        }

        return zobristHash;
    }

    // first generate opponent's moves BUT DON'T ADD THEM TO moves, and after that generate current player's moves and add the to moves
    public List<ZMove> generateMoves() {
        // clear previous moves from last turn
        moves.clear();

        int pawnsIndex = 0;
        int knightsIndex = 0;
        int bishopsIndex = 0;
        int rooksIndex = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!isEmpty(row, col)) {
                    // let blacks generate moves
                    if (currentPlayer == WHITE_PLAYER) {
                        switch (displayBoard[row][col]) {
                            case 'p' -> {
                                generatePawnMoves(row, col);
                                bPawnsLocations[pawnsIndex][0] = row;
                                bPawnsLocations[pawnsIndex][1] = col;
                                pawnsIndex++;
                            }
                            case 'n' -> {
                                generateKnightMoves(row, col);
                                bKnightsLocations[knightsIndex][0] = row;
                                bKnightsLocations[knightsIndex][1] = col;
                                knightsIndex++;
                            }
                            case 'b' -> {
                                generateSlidingPiecesMoves(row, col, bishopDirections);
                                bBishopsLocations[bishopsIndex][0] = row;
                                bBishopsLocations[bishopsIndex][1] = col;
                                bishopsIndex++;
                            }
                            case 'r' -> {
                                generateSlidingPiecesMoves(row, col, rookDirections);
                                bRooksLocations[rooksIndex][0] = row;
                                bRooksLocations[rooksIndex][1] = col;
                                rooksIndex++;
                            }
                            case 'q' -> {
                                generateSlidingPiecesMoves(row, col, queenDirections);
                                bQueenLocation[0][0] = row;
                                bQueenLocation[0][1] = col;
                            }
                            case 'k' -> {
                                generateKingMoves(row, col);
                                bKingLocation[0][0] = row;
                                bKingLocation[0][1] = col;
                            }
                        }
                        // let whites generate moves
                    } else {
                        switch (displayBoard[row][col]) {
                            case 'P' -> {
                                generatePawnMoves(row, col);
                                wPawnsLocations[pawnsIndex][0] = row;
                                wPawnsLocations[pawnsIndex][1] = col;
                                pawnsIndex++;
                            }
                            case 'N' -> {
                                generateKnightMoves(row, col);
                                wKnightsLocations[knightsIndex][0] = row;
                                wKnightsLocations[knightsIndex][1] = col;
                                knightsIndex++;
                            }
                            case 'B' -> {
                                generateSlidingPiecesMoves(row, col, bishopDirections);
                                wBishopsLocations[bishopsIndex][0] = row;
                                wBishopsLocations[bishopsIndex][1] = col;
                                bishopsIndex++;
                            }
                            case 'R' -> {
                                generateSlidingPiecesMoves(row, col, rookDirections);
                                wRooksLocations[rooksIndex][0] = row;
                                wRooksLocations[rooksIndex][1] = col;
                                rooksIndex++;
                            }
                            case 'Q' -> {
                                generateSlidingPiecesMoves(row, col, queenDirections);
                                wQueenLocation[0][0] = row;
                                wQueenLocation[0][1] = col;
                            }
                            case 'K' -> {
                                generateKingMoves(row, col);
                                wKingLocation[0][0] = row;
                                wKingLocation[0][1] = col;
                            }
                        }
                    }
                }
            }
        }

        // generate whites and add them to moves
        if (currentPlayer == WHITE_PLAYER) {
            for (int i = 0; i < wPawnsLocations.length; i++) {
                int row = wPawnsLocations[i][0];
                int col = wPawnsLocations[i][1];
                moves.addAll(generatePawnMoves(row, col));
            }
            for (int i = 0; i < wKnightsLocations.length; i++) {
                int row = wKnightsLocations[i][0];
                int col = wKnightsLocations[i][1];
                moves.addAll(generateKnightMoves(row, col));
            }
            for (int i = 0; i < wBishopsLocations.length; i++) {
                int row = wBishopsLocations[i][0];
                int col = wBishopsLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(row, col, bishopDirections));
            }
            for (int i = 0; i < wRooksLocations.length; i++) {
                int row = wRooksLocations[i][0];
                int col = wRooksLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(row, col, rookDirections));
            }
            int queenRow = wQueenLocation[0][0];
            int queenCol = wQueenLocation[0][1];
            moves.addAll(generateSlidingPiecesMoves(queenRow, queenCol, queenDirections));

            int kingRow = wKingLocation[0][0];
            int kingCol = wKingLocation[0][1];
            moves.addAll(generateKingMoves(kingRow, kingCol));
        } else if (currentPlayer == BLACK_PLAYER) {
            for (int i = 0; i < bPawnsLocations.length; i++) {
                int row = bPawnsLocations[i][0];
                int col = bPawnsLocations[i][1];
                moves.addAll(generatePawnMoves(row, col));
            }
            for (int i = 0; i < bKnightsLocations.length; i++) {
                int row = bKnightsLocations[i][0];
                int col = bKnightsLocations[i][1];
                moves.addAll(generateKnightMoves(row, col));
            }
            for (int i = 0; i < bBishopsLocations.length; i++) {
                int row = bBishopsLocations[i][0];
                int col = bBishopsLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(row, col, bishopDirections));
            }
            for (int i = 0; i < bRooksLocations.length; i++) {
                int row = bRooksLocations[i][0];
                int col = bRooksLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(row, col, rookDirections));
            }
            int queenRow = bQueenLocation[0][0];
            int queenCol = bQueenLocation[0][1];
            moves.addAll(generateSlidingPiecesMoves(queenRow, queenCol, queenDirections));

            int kingRow = bKingLocation[0][0];
            int kingCol = bKingLocation[0][1];
            moves.addAll(generateKingMoves(kingRow, kingCol));
        }

        return moves;
    }

    public void calculateZobristHash() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (displayBoard[row][col] != '-') {
                    int pieceBoardIndex = piecesTypeValuesMap.get(displayBoard[row][col]);
                    zobristHash ^= board[pieceBoardIndex][row][col];
                }
            }
        }
    }

    private List<ZMove> generatePawnMoves(int row, int col) {
        List<ZMove> moves = new ArrayList<>();

        int[] captureDirections = new int[] {-1, 1};

        boolean isPawnWhite = isWhitePiece(displayBoard[row][col]);
        boolean isPawnBlack = isBlackPiece(displayBoard[row][col]);

        int player = 0;
        if (isPawnWhite) player = WHITE_PLAYER;
        else if (isPawnBlack) player = BLACK_PLAYER;

        int direction = 0;
        if (isPawnWhite) direction = 1;
        else if (isPawnBlack) direction = -1;

        if (row + direction < 8 && row + direction >= 0) {
            if (isEmpty(row + direction, col)) {
                ZMove move = new ZMove(row, col, row + direction, col);
                moves.add(move);

                // at the first row in the game
                // no need to check boundaries
                if (row == pawnsStartRow.get(player)) {
                    if (isEmpty(row + 2*direction, col)) {
                        ZMove longMove = new ZMove(row, col, row + 2*direction, col);
                        moves.add(longMove);
                    }
                }
            }
        }

        // captures
        for (int d : captureDirections) {
            if (row + direction >= 8 || row + direction < 0 || col + d >= 8 || col + d < 0) continue;

            if (isPawnWhite) {
                if (isEmpty(row + direction, col + d)) whiteThreateningTiles[row + direction][col + d] = true;
                else if (isBlackPiece(displayBoard[row + direction][col + d])) {
                    ZMove move = new ZMove(row, col, row + direction, col + d);
                    moves.add(move);
                    whiteThreateningTiles[row + direction][col + d] = true;
                }
                // en passant
                if (displayBoard[row][col + d] == 'p' && row == enPassantRows.get(player) && isEmpty(row + direction, col + d)) {
                    ZMove move = new ZMove(row, col, row + direction, col + d);
                    moves.add(move);
                    whiteThreateningTiles[row + direction][col + d] = true;
                }
            } else if (isBlackPiece(displayBoard[row][col])) {
                if (isEmpty(row + direction, col + d)) blackThreateningTiles[row + direction][col + d] = true;
                else if (isWhitePiece(displayBoard[row + direction][col + d])) {
                    ZMove move = new ZMove(row, col, row + direction, col + d);
                    moves.add(move);
                    blackThreateningTiles[row + direction][col + d] = true;
                }
                if (displayBoard[row][col + d] == 'P' && row == enPassantRows.get(player) && isEmpty(row + direction, col + d)) {
                    ZMove move = new ZMove(row, col, row + direction, col + d);
                    moves.add(move);
                    blackThreateningTiles[row + direction][col + d] = true;
                }
            }
        }
        return moves;
    }

    private List<ZMove> generateKnightMoves(int row, int col) {
        List<ZMove> moves = new ArrayList<>();

        boolean isKnightWhite = isWhitePiece(displayBoard[row][col]);
        boolean isKnightBlack = isBlackPiece(displayBoard[row][col]);

        int[][] directions = new int[][] {
                {2, 1},
                {2, -1},
                {-2, 1},
                {-2, -1},
                {1, 2},
                {1, -2},
                {-1, 2},
                {-1, -2}
        };

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            if (row + r >= 8 || row + r < 0 || col + c >= 8 || col + c < 0) continue;

            if (isEmpty(row + r, col + c)) {
                ZMove move = new ZMove(row, col, row + r, col + c);
                moves.add(move);
            }
            if (isKnightWhite) {
                if (isBlackPiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    whiteThreateningTiles[row + r][col + c] = true;
                }
            } else if (isKnightBlack) {
                if (isWhitePiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                }
            }
        }
        return moves;
    }

    private List<ZMove> generateSlidingPiecesMoves(int row, int col, int[][] directions) {
        List<ZMove> moves = new ArrayList<>();

        boolean isSlidingPieceWhite = isWhitePiece(displayBoard[row][col]);
        boolean isSlidingPieceBlack = isBlackPiece(displayBoard[row][col]);

        for (int[] direction : directions) {
            int r = direction[0];
            int c = direction[1];

            // multiplier of directions
            for (int i = 1; i < 8; i++) {
                if (row + r*i >= 8 || row + r*i < 0 || col + c*i >= 8 || col + c*i < 0) continue;

                // empty
                if (isEmpty(row + r*i, col + c*i)) {
                    ZMove move = new ZMove(row, col, row + r*i, col + c*i);
                    moves.add(move);
                    if (isSlidingPieceWhite) whiteThreateningTiles[row + r*i][col + c*i] = true;
                    else blackThreateningTiles[row + r*i][col + c*i] = true;
                    // not empty
                } else if (!isEmpty(row + r*i, col + c*i)) {
                    // white piece
                    if (isWhitePiece(displayBoard[row + r*i][col + c*i])) {
                        if (isSlidingPieceBlack) {
                            ZMove move = new ZMove(row, col, row + r*i, col + c*i);
                            moves.add(move);
                        }
                        // black piece
                    } else if (isBlackPiece(displayBoard[row + r*i][col + c*i])) {
                        if (isSlidingPieceWhite) {
                            ZMove move = new ZMove(row, col, row + r*i, col + c*i);
                            moves.add(move);
                        }
                    }
                    if (isSlidingPieceWhite) whiteThreateningTiles[row + r*i][col + c*i] = true;
                    else if (isSlidingPieceBlack) blackThreateningTiles[row + r*i][col + c*i] = true;
                    break;
                }
            }
        }
        // setting threats
        moves.forEach(move -> {
            if (isSlidingPieceWhite) whiteThreateningTiles[move.getTargetRow()][move.getTargetCol()] = true;
            else blackThreateningTiles[move.getTargetRow()][move.getTargetCol()] = true;
        });
        return moves;
    }

    private List<ZMove> generateKingMoves(int row, int col) {
        List<ZMove> moves = new ArrayList<>();

        boolean isKingWhite = isWhitePiece(displayBoard[row][col]);
        boolean isKingBlack = isBlackPiece(displayBoard[row][col]);

        // king's directions are just like queen's but it's not sliding
        for (int[] direction : queenDirections) {
            int r = direction[0];
            int c = direction[1];

            if (row + r >= 8 || row + r < 0 || col + c >= 8 || col + c < 0) continue;

            if (isKingWhite) {
                // checking if tile is threatened
                if (blackThreateningTiles[row + r][col + c]) {
                    continue;
                }
                if (isEmpty(row + r, col + c)) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    whiteThreateningTiles[row + r][col + c] = true;
                } else if (isWhitePiece(displayBoard[row + r][col + c])) {
                    whiteThreateningTiles[row + r][col + c] = true;
                    continue;
                }
                else if (isBlackPiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    whiteThreateningTiles[row + r][col + c] = true;
                }
            } else if (isKingBlack) {
                // checking if tile is threatened
                if (whiteThreateningTiles[row + r][col + c]) {
                    continue;
                }
                if (isEmpty(row + r, col + c)) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                } else if (isWhitePiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                    continue;
                }
                else if (isBlackPiece(displayBoard[row + r][col + c])) {
                    blackThreateningTiles[row + r][col + c] = true;
                }
            }
        }
        // TODO: add castling

        return moves;
    }

    private boolean isWhitePiece(char piece) {
        return Character.isUpperCase(piece);
    }

    private boolean isBlackPiece(char piece) {
        return Character.isLowerCase(piece);
    }

    private boolean isEmpty(int row, int col) {
        return displayBoard[row][col] == '-';
    }

    public void printBoard() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                System.out.print(displayBoard[r][c] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    private static final Path path = Paths.get("");
    private static final String currentPath = path.toAbsolutePath().toString();

    public char[][] getDisplayBoard() {
        return displayBoard;
    }
}
