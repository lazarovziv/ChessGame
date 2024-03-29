package com.zivlazarov.newengine.model;

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

    private final Stack<Map<Character, ZMove>> historyPlays = new Stack<>();

    private int currentPlayer = 0;

    private final Map<Character, Integer> piecesTypeValuesMap = new HashMap<>();

    private final Map<Integer, Integer> pawnsStartRow = Map.of(
            WHITE_PLAYER, 1,
            BLACK_PLAYER, 6
    );

    private final Map<Integer, Integer> pawnsLastRow = Map.of(
            WHITE_PLAYER, 7,
            BLACK_PLAYER, 0
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

    private static boolean hasWhiteKingCastled = false;
    private static boolean addedScoreForWK = false;
    private static boolean hasWhiteQueenCastled = false;
    private static boolean addedScoreForWQ = false;
    private static boolean hasBlackKingCastled = false;
    private static boolean addedScoreForBK = false;
    private static boolean hasBlackQueenCastled = false;
    private static boolean addedScoreForBQ = false;

    private static boolean canWhiteKingCastle = false;
    private static boolean canWhiteQueenCastle = false;
    private static boolean canBlackKingCastle = false;
    private static boolean canBlackQueenCastle = false;

    private final int[][] wPawnsLocations = new int[16][8];
    private final int[][] wKnightsLocations = new int[16][2];
    private final int[][] wBishopsLocations = new int[16][2];;
    private final int[][] wRooksLocations = new int[16][2];;
    private final int[][] wQueenLocations = new int[9][2];;
    private final int[] wKingLocation = new int[2];;

    private final int[][] bPawnsLocations = new int[16][8];
    private final int[][] bKnightsLocations = new int[16][2];
    private final int[][] bBishopsLocations = new int[16][2];;
    private final int[][] bRooksLocations = new int[16][2];;
    private final int[][] bQueenLocations = new int[9][2];;
    private final int[] bKingLocation = new int[2];;

    private static final int WHITE_PLAYER = 1;
    private static final int BLACK_PLAYER = -1;

    private static final double[][] wPawnStrongTiles = new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0},
            {1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0},
            {0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5},
            {0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0},
            {0.5, -0.5, -1.0, 0.0, 0.0, -1.0, -0.5, 0.5},
            {0.5, 1.0, 1.0, -2.0, -2.0, 1.0, 1.0, 0.5},
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
    };
    private static final double[][] wKnightStrongTiles = new double[][] {
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
            {-4.0, -2.0, 0.0, 0.0, 0.0, 0.0, -2.0, -4.0},
            {-3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0},
            {-3.0, 0.5, 1.5, 2.0, 2.0, 1.5, 0.5, -3.0},
            {-3.0, 0.0, 1.5, 2.0, 2.0, 1.5, 0.0, -3.0},
            {-3.0, 0.5, 1.0, 1.5, 1.5, 1.0, 0.5, -3.0},
            {-4.0, -2.0, 0.0, 0.5, 0.5, 0.0, -2.0, -4.0},
            {-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0}
    };
    private static final double[][] wBishopStrongTiles = new double[][] {
            {-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
            {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0},
            {-1.0, 0.0, 0.5, 1.0, 1.0, 0.5, 0.0, -1.0},
            {-1.0, 0.5, 0.5, 1.0, 1.0, 0.5, 0.5, -1.0},
            {-1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.0},
            {-1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, -1.0},
            {-1.0, 0.5, 0.0, 0.0, 0.0, 0.0, 0.5, -1.0},
            {-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
    };
    private static final double[][] wRookStrongTiles = new double[][] {
            {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
            {0.5, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.5},
            {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
            {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
            {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
            {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
            {-0.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -0.5},
            {0.0, 0.0, 0.0, 0.5, 0.5, 0.0, 0.0, 0.0}
    };
    private static final double[][] wQueenStrongTiles = new double[][] {
            {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
            {-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0},
            {-1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
            {-0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
            {0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5},
            {-1.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0},
            {-1.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.0, -1.0},
            {-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
    };
    private static final double[][] wKingStrongTiles = new double[][] {
            {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            {-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
            {-2.0, -3.0, -3.0, -4.0 ,-4.0, -3.0, -3.0, -2.0},
            {-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
            {2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0},
            {2.0, 3.0, 1.0, 0.0, 0.0, 1.0, 3.0, 2.0}
    };

    private static final double[][] bPawnStrongTiles = revertStrongTiles(wPawnStrongTiles);
    private static final double[][] bKnightStrongTiles = revertStrongTiles(wKnightStrongTiles);
    private static final double[][] bBishopStrongTiles = revertStrongTiles(wBishopStrongTiles);
    private static final double[][] bRookStrongTiles = revertStrongTiles(wRookStrongTiles);
    private static final double[][] bQueenStrongTiles = revertStrongTiles(wQueenStrongTiles);
    private static final double[][] bKingStrongTiles = revertStrongTiles(wKingStrongTiles);

    public static final Map<Character, String> piecesImagesMap = new HashMap<>();
    private static final Path path = Paths.get("");
    private static final String currentPath = path.toAbsolutePath().toString();

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

        // setting initial pieces' positions in board to their respective locations' arrays
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
                            wQueenLocations[0][0] = row;
                            wQueenLocations[0][1] = col;
                        }
                        case 'K' -> {
                            wKingLocation[0] = row;
                            wKingLocation[1] = col;
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
                            bQueenLocations[0][0] = row;
                            bQueenLocations[0][1] = col;
                        }
                        case 'k' -> {
                            bKingLocation[0] = row;
                            bKingLocation[1] = col;
                        }
                    }
                }
            }
        }

        currentPlayer = WHITE_PLAYER;

        calculateZobristHash();
        transpositionTable.put(zobristHash, board);
    }

    public long makeMove(ZMove move) {
        if (!moves.contains(move)) return zobristHash;

        char piece = displayBoard[move.getSourceRow()][move.getSourceCol()];
        if (piece == '-') return 0L;
        int pieceBoardIndex = piecesTypeValuesMap.get(piece);

        // first, emptying piece's source tile
        displayBoard[move.getSourceRow()][move.getSourceCol()] = '-';
        zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];

        switch (move.getMoveLabel()) {
            case REGULAR -> {
                // setting piece in target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
            }
            case CAPTURE -> {
                char capturedPiece = move.getCapturedPiece();
                System.out.println(capturedPiece + " CAPTURE");
                int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                // emptying target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetRow()] = '-';
                zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                // setting piece in target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
            }
            case EN_PASSANT -> {
                if (currentPlayer == WHITE_PLAYER) {
//                    char capturedPiece = displayBoard[move.getTargetRow() - 1][move.getTargetCol()];
                    char capturedPiece = move.getCapturedPiece();
                    int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                    // emptying the captured piece's tile and hashing
                    // -1 is the tile "behind" piece
                    displayBoard[move.getTargetRow() - 1][move.getTargetCol()] = '-';
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow() - 1][move.getTargetCol()];
                    // setting piece in target tile and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                    zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                } else if (currentPlayer == BLACK_PLAYER) {
//                    char capturedPiece = displayBoard[move.getTargetRow() + 1][move.getTargetCol()];
                    char capturedPiece = move.getCapturedPiece();
                    int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);

                    // emptying the captured piece's tile and hashing
                    // +1 is the tile "behind" piece
                    displayBoard[move.getTargetRow() + 1][move.getTargetCol()] = '-';
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow() + 1][move.getTargetCol()];
                    // setting piece in target tile and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                    zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                }
            }
            case KING_SIDE_CASTLE -> {
                // setting piece in target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                if (currentPlayer == WHITE_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('R');
                    // emptying rook tile and hashing
                    displayBoard[0][7] = '-';
                    zobristHash ^= board[rookBoardIndex][0][7];
                    // setting rook in castle tile and hashing
                    displayBoard[0][5] = 'R';
                    zobristHash ^= board[rookBoardIndex][0][5];
                    if (!hasWhiteKingCastled) hasWhiteKingCastled = true;
                } else if (currentPlayer == BLACK_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('r');
                    // emptying rook tile and hashing
                    displayBoard[7][7] = '-';
                    zobristHash ^= board[rookBoardIndex][7][7];
                    // setting rook in castle tile and hashing
                    displayBoard[7][5] = 'r';
                    zobristHash ^= board[rookBoardIndex][7][5];
                    if (!hasBlackKingCastled) hasBlackKingCastled = true;
                }
            }
            case QUEEN_SIDE_CASTLE -> {
                // setting piece in target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                if (currentPlayer == WHITE_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('R');
                    // emptying rook tile and hashing
                    displayBoard[0][0] = '-';
                    zobristHash ^= board[rookBoardIndex][0][7];
                    // setting rook in castle tile and hashing
                    displayBoard[0][3] = 'R';
                    zobristHash ^= board[rookBoardIndex][0][4];
                    if (!hasWhiteQueenCastled) hasWhiteQueenCastled = true;
                } else if (currentPlayer == BLACK_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('r');
                    // emptying rook tile and hashing
                    displayBoard[7][0] = '-';
                    zobristHash ^= board[rookBoardIndex][7][0];
                    // setting rook in castle tile and hashing
                    displayBoard[7][3] = 'r';
                    zobristHash ^= board[rookBoardIndex][7][3];
                    if (!hasBlackQueenCastled) hasBlackQueenCastled = true;
                }
            }
            case PAWN_PROMOTION_REGULAR -> {
                if (currentPlayer == WHITE_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('Q');
                    // setting piece in target tile and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = 'Q';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                } else if (currentPlayer == BLACK_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('q');
                    // setting piece in target tile and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = 'q';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                }
            }
            case PAWN_PROMOTION_CAPTURE -> {
                char capturedPiece = move.getCapturedPiece();
                int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                // emptying captured piece's tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];

                if (currentPlayer == WHITE_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('Q');
                    // setting piece in target tile and changing piece's type and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = 'Q';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                } else if (currentPlayer == BLACK_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('q');
                    // setting piece in target tile and changing piece's type and hashing
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = 'q';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                }
            }

        }

        // adding current board to the transposition table
        transpositionTable.put(zobristHash, board);

        // changing turns
        currentPlayer *= -1;

        // hashing if it's black's turn to move
        if (currentPlayer == BLACK_PLAYER) {
            zobristHash ^= blackToMove;
        }

        // adding move to historyPlays
        Map<Character, ZMove> map = Map.of(displayBoard[move.getTargetRow()][move.getTargetCol()], move);
        historyPlays.push(map);

        return zobristHash;
    }

    public long unmakeMove(ZMove move) {
        // if it was supposed to be black's turn, we hashed him, so now we need to hash him back to return to previous hash
        if (currentPlayer == BLACK_PLAYER) {
            zobristHash ^= blackToMove;
        }
        // changing back the turn
        currentPlayer *= -1;

        char piece = displayBoard[move.getTargetRow()][move.getTargetCol()];
        if (piece == '-') return 0L;
        int pieceBoardIndex = piecesTypeValuesMap.get(piece);

        switch (move.getMoveLabel()) {
            case REGULAR -> {
                // setting target tile empty
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                // moving the piece back
                displayBoard[move.getSourceRow()][move.getSourceCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
            }
            case CAPTURE -> {
                char capturedPiece = move.getCapturedPiece();
                int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                // setting target tile empty
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                // setting captured piece in move's target tile and hashing
                displayBoard[move.getTargetRow()][move.getTargetCol()] = capturedPiece;
                zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                // moving the piece back
                displayBoard[move.getSourceRow()][move.getSourceCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
            }
            case EN_PASSANT -> {
                char capturedPiece = move.getCapturedPiece();
                int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                // setting target tile empty
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                // black piece was captured because we reverted the turn back to white
                if (currentPlayer == WHITE_PLAYER) {
                    // setting captured pawn at its previous tile and hashing
                    displayBoard[move.getTargetRow() - 1][move.getTargetCol()] = capturedPiece;
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow() - 1][move.getTargetCol()];
                    // white piece was captured
                } else if (currentPlayer == BLACK_PLAYER) {
                    // setting captured pawn at its previous tile and hashing
                    displayBoard[move.getTargetRow() + 1][move.getTargetCol()] = capturedPiece;
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow() + 1][move.getTargetCol()];
                }
                // moving the piece back
                displayBoard[move.getSourceRow()][move.getSourceCol()] = piece;
                zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
            }
            case KING_SIDE_CASTLE -> {
                // setting target tile empty
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                if (currentPlayer == WHITE_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('R');
                    // emptying king side castle move
                    displayBoard[0][5] = '-';
                    zobristHash ^= board[rookBoardIndex][0][5];
                    // setting king side rook to original tile
                    displayBoard[0][7] = 'R';
                    zobristHash ^= board[rookBoardIndex][0][7];
                    // setting king in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'K';
                    zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
                    if (hasWhiteKingCastled) hasWhiteKingCastled = false;
                } else if (currentPlayer == BLACK_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('r');
                    // emptying king side castle move
                    displayBoard[7][5] = '-';
                    zobristHash ^= board[rookBoardIndex][7][5];
                    // setting king side rook to original tile
                    displayBoard[7][7] = 'r';
                    zobristHash ^= board[rookBoardIndex][7][7];
                    // setting king in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'k';
                    zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
                    if (hasBlackKingCastled) hasBlackKingCastled = false;
                }
            }
            case QUEEN_SIDE_CASTLE -> {
                // setting target tile empty
                displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                zobristHash ^= board[pieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                if (currentPlayer == WHITE_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('R');
                    // emptying queen side castle move
                    displayBoard[0][3] = '-';
                    zobristHash ^= board[rookBoardIndex][0][3];
                    // setting queen side rook to original tile
                    displayBoard[0][0] = 'r';
                    zobristHash ^= board[rookBoardIndex][0][0];
                    // setting king in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'K';
                    zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
                    if (hasWhiteQueenCastled) hasWhiteQueenCastled = false;
                } else if (currentPlayer == BLACK_PLAYER) {
                    int rookBoardIndex = piecesTypeValuesMap.get('r');
                    // emptying queen side castle move
                    displayBoard[7][3] = '-';
                    zobristHash ^= board[rookBoardIndex][7][3];
                    // setting queen side rook to original tile
                    displayBoard[7][0] = 'r';
                    zobristHash ^= board[rookBoardIndex][7][0];
                    // setting king in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'k';
                    zobristHash ^= board[pieceBoardIndex][move.getSourceRow()][move.getSourceCol()];
                    if (hasBlackQueenCastled) hasBlackQueenCastled = false;
                }
            }
            case PAWN_PROMOTION_REGULAR -> {
                if (currentPlayer == WHITE_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('Q');
                    int pawnBoardIndex = piecesTypeValuesMap.get('P');
                    // emptying target tile and hashing with queen's table
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting pawn back
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'P';
                    zobristHash ^= board[pawnBoardIndex][move.getSourceRow()][move.getSourceCol()];
                } else if (currentPlayer == BLACK_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('q');
                    int pawnBoardIndex = piecesTypeValuesMap.get('p');
                    // emptying target tile and hashing with queen's table
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting pawn back
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'p';
                    zobristHash ^= board[pawnBoardIndex][move.getSourceRow()][move.getSourceCol()];
                }
            }
            case PAWN_PROMOTION_CAPTURE -> {
                char capturedPiece = move.getCapturedPiece();
                int capturedPieceBoardIndex = piecesTypeValuesMap.get(capturedPiece);
                if (currentPlayer == WHITE_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('Q');
                    int pawnBoardIndex = piecesTypeValuesMap.get('P');
                    // emptying target tile and hashing with queen's table
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting captured piece back in tile
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting pawn back in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'P';
                    zobristHash ^= board[pawnBoardIndex][move.getSourceRow()][move.getSourceCol()];
                } else if (currentPlayer == BLACK_PLAYER) {
                    int queenBoardIndex = piecesTypeValuesMap.get('q');
                    int pawnBoardIndex = piecesTypeValuesMap.get('p');
                    // emptying target tile and hashing with queen's table
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = '-';
                    zobristHash ^= board[queenBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting captured piece back in tile
                    displayBoard[move.getTargetRow()][move.getTargetCol()] = piece;
                    zobristHash ^= board[capturedPieceBoardIndex][move.getTargetRow()][move.getTargetCol()];
                    // setting pawn back in source tile
                    displayBoard[move.getSourceRow()][move.getSourceCol()] = 'p';
                    zobristHash ^= board[pawnBoardIndex][move.getSourceRow()][move.getSourceCol()];
                }
            }
        }

        if (historyPlays.size() > 0) historyPlays.remove(historyPlays.lastElement());

        return zobristHash;
    }

    // first generate opponent's moves BUT DON'T ADD THEM TO moves, and after that generate current player's moves and add the to moves
    public List<ZMove> generateMoves() {
        // clear previous moves from last turn
        moves.clear();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (currentPlayer == WHITE_PLAYER) {
                    whiteThreateningTiles[i][j] = false;
                } else if (currentPlayer == BLACK_PLAYER) {
                    blackThreateningTiles[i][j] = false;
                }
            }
        }

        canWhiteKingCastle = false;
        canWhiteQueenCastle = false;
        canBlackKingCastle = false;
        canBlackQueenCastle = false;

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
                                bQueenLocations[0][0] = row;
                                bQueenLocations[0][1] = col;
                            }
                            case 'k' -> {
                                generateKingMoves(row, col);
                                bKingLocation[0] = row;
                                bKingLocation[1] = col;
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
                                wQueenLocations[0][0] = row;
                                wQueenLocations[0][1] = col;
                            }
                            case 'K' -> {
                                generateKingMoves(row, col);
                                wKingLocation[0] = row;
                                wKingLocation[1] = col;
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
            for (int i = 0; i < wQueenLocations.length; i++) {
                int queenRow = wQueenLocations[i][0];
                int queenCol = wQueenLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(queenRow, queenCol, queenDirections));
            }

            int kingRow = wKingLocation[0];
            int kingCol = wKingLocation[1];
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
            for (int i = 0; i < bQueenLocations.length; i++) {
                int queenRow = bQueenLocations[i][0];
                int queenCol = bQueenLocations[i][1];
                moves.addAll(generateSlidingPiecesMoves(queenRow, queenCol, queenDirections));
            }

            int kingRow = bKingLocation[0];
            int kingCol = bKingLocation[1];
            moves.addAll(generateKingMoves(kingRow, kingCol));
        }

        if (isPlayerInCheck(currentPlayer)) {
            List<ZMove> legalMoves = searchLegalMovesWhenInCheck();
            if (legalMoves.size() > 0) {
                moves.clear();
                moves.addAll(legalMoves);
            } // else checkmate
        }

        return moves;
    }

    private List<ZMove> searchLegalMovesWhenInCheck() {
        List<ZMove> legalMoves = new ArrayList<>();
        List<ZMove> possibleLegalMoves = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (currentPlayer == WHITE_PLAYER) {
                    if (blackThreateningTiles[i][j]) {
                        final int row = i;
                        final int col = j;
                        possibleLegalMoves.addAll(moves.stream().filter(m -> m.getTargetRow() == row && m.getTargetCol() == col).toList());
                    }
                } else if (currentPlayer == BLACK_PLAYER) {
                    if (whiteThreateningTiles[i][j]) {
                        final int row = i;
                        final int col = j;
                        possibleLegalMoves.addAll(moves.stream().filter(m -> m.getTargetRow() == row && m.getTargetCol() == col).toList());
                    }
                }
            }
        }

        for (ZMove move : possibleLegalMoves) {
            // changing currentPlayer value to opponent
            makeMove(move);
            // generating moves for opponent player
            generateMoves();
            // checking for original currentPlayer
            if (!isPlayerInCheck(currentPlayer * -1)) {
                legalMoves.add(move);
            }
            // changing to original
            unmakeMove(move);
        }

        return legalMoves;
    }

    private List<ZMove> generatePawnMoves(int row, int col) {
        List<ZMove> moves = new ArrayList<>();

        if (isEmpty(row, col)) return moves;

        int[] captureDirections = new int[] {-1, 1};

        boolean isPawnWhite = isWhitePiece(displayBoard[row][col]);
        boolean isPawnBlack = isBlackPiece(displayBoard[row][col]);

        int player = -1;
        if (isPawnWhite) player = WHITE_PLAYER;
//        else if (isPawnBlack) player = BLACK_PLAYER;

        int direction = 0;
        if (isPawnWhite) direction = 1;
        else if (isPawnBlack) direction = -1;

        if (row + direction < 8 && row + direction >= 0) {
            if (isEmpty(row + direction, col)) {
                ZMove move = new ZMove(row, col, row + direction, col);
                // checking if reached last row for promotion
                if (row + direction == pawnsLastRow.get(player)) {
                    move.setMoveLabel(ZMoveLabel.PAWN_PROMOTION_REGULAR);
                } else move.setMoveLabel(ZMoveLabel.REGULAR);
                moves.add(move);

                // at the first row in the game
                // no need to check boundaries
                if (row == pawnsStartRow.get(player)) {
                    if (isEmpty(row + 2*direction, col)) {
                        ZMove longMove = new ZMove(row, col, row + 2*direction, col);
                        move.setMoveLabel(ZMoveLabel.REGULAR);
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
                    // checking if reached last row for promotion
                    if (row + direction == pawnsLastRow.get(player)) {
                        move.setMoveLabel(ZMoveLabel.PAWN_PROMOTION_CAPTURE);
                        move.setCapturedPiece(displayBoard[row + direction][col + d]);
                    } else move.setMoveLabel(ZMoveLabel.CAPTURE);
                    moves.add(move);
                    whiteThreateningTiles[row + direction][col + d] = true;
                }
                // en passant
                if (displayBoard[row][col + d] == 'p' && row == enPassantRows.get(player) && isEmpty(row + direction, col + d)) {
                    // checking if last pawn's move was from the starting row
                    if (historyPlays.lastElement().containsKey('p') &&
                            historyPlays.lastElement().get('p').getSourceRow() == pawnsStartRow.get(BLACK_PLAYER) &&
                            historyPlays.lastElement().get('p').getSourceCol() == col + d) {
                        ZMove move = new ZMove(row, col, row + direction, col + d);
                        move.setMoveLabel(ZMoveLabel.EN_PASSANT);
                        move.setCapturedPiece(displayBoard[row][col + d]);
                        moves.add(move);
                        whiteThreateningTiles[row + direction][col + d] = true;
                    }
                }
            } else if (isPawnBlack) {
                if (isEmpty(row + direction, col + d)) blackThreateningTiles[row + direction][col + d] = true;
                else if (isWhitePiece(displayBoard[row + direction][col + d])) {
                    ZMove move = new ZMove(row, col, row + direction, col + d);
                    // checking if reached last row for promotion
                    if (row + direction == pawnsLastRow.get(player)) {
                        move.setMoveLabel(ZMoveLabel.PAWN_PROMOTION_CAPTURE);
                        move.setCapturedPiece(displayBoard[row + direction][col + d]);
                    } else move.setMoveLabel(ZMoveLabel.CAPTURE);
                    moves.add(move);
                    blackThreateningTiles[row + direction][col + d] = true;
                }
                // en passant
                if (displayBoard[row][col + d] == 'P' && row == enPassantRows.get(player) && isEmpty(row + direction, col + d)) {
                    // checking if last pawn's move was from the starting row
                    if (historyPlays.lastElement().containsKey('P') &&
                            historyPlays.lastElement().get('P').getSourceRow() == pawnsStartRow.get(WHITE_PLAYER) &&
                            historyPlays.lastElement().get('P').getSourceCol() == col + d) {
                        ZMove move = new ZMove(row, col, row + direction, col + d);
                        move.setMoveLabel(ZMoveLabel.EN_PASSANT);
                        move.setCapturedPiece(displayBoard[row][col + d]);
                        moves.add(move);
                        blackThreateningTiles[row + direction][col + d] = true;
                    }
                }
            }

        }
        return moves;
    }

    private List<ZMove> generateKnightMoves(int row, int col) {
        List<ZMove> moves = new ArrayList<>();

        if (isEmpty(row, col)) return moves;

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
                move.setMoveLabel(ZMoveLabel.REGULAR);
                moves.add(move);
            }
            if (isKnightWhite) {
                if (isBlackPiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    move.setMoveLabel(ZMoveLabel.CAPTURE);
                    move.setCapturedPiece(displayBoard[row + r][col + c]);
                    moves.add(move);
                    whiteThreateningTiles[row + r][col + c] = true;
                }
            } else if (isKnightBlack) {
                if (isWhitePiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    move.setMoveLabel(ZMoveLabel.CAPTURE);
                    move.setCapturedPiece(displayBoard[row + r][col + c]);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                }
            }
        }
        return moves;
    }

    private List<ZMove> generateSlidingPiecesMoves(int row, int col, int[][] directions) {
        List<ZMove> moves = new ArrayList<>();

        if (isEmpty(row, col)) return moves;

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
                    move.setMoveLabel(ZMoveLabel.REGULAR);
                    moves.add(move);
                    if (isSlidingPieceWhite) whiteThreateningTiles[row + r*i][col + c*i] = true;
                    else blackThreateningTiles[row + r*i][col + c*i] = true;
                    // not empty
                } else if (!isEmpty(row + r*i, col + c*i)) {
                    // white piece
                    if (isWhitePiece(displayBoard[row + r*i][col + c*i])) {
                        if (isSlidingPieceBlack) {
                            ZMove move = new ZMove(row, col, row + r*i, col + c*i);
                            move.setMoveLabel(ZMoveLabel.CAPTURE);
                            move.setCapturedPiece(displayBoard[row + r*i][col + c*i]);
                            moves.add(move);
                        }
                        // black piece
                    } else if (isBlackPiece(displayBoard[row + r*i][col + c*i])) {
                        if (isSlidingPieceWhite) {
                            ZMove move = new ZMove(row, col, row + r*i, col + c*i);
                            move.setMoveLabel(ZMoveLabel.CAPTURE);
                            move.setCapturedPiece(displayBoard[row + r*i][col + c*i]);
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
        // suggestion: before generating, generate all other pieces' moves (?)
        List<ZMove> moves = new ArrayList<>();

        if (isEmpty(row, col)) return moves;

        boolean isKingWhite = isWhitePiece(displayBoard[row][col]);
        boolean isKingBlack = isBlackPiece(displayBoard[row][col]);

        boolean kingSideClear = true;
        boolean queenSideClear = true;

        // white castling
        if (isKingWhite) {
            if (row == 0 && col == 4) {
                // king side castle
                for (int i = 1; i < 3; i++) {
                    // if tiles between king and rooks are empty and aren't threatened
                    if (!isEmpty(row, col + i) || blackThreateningTiles[row][col + i]) {
                        kingSideClear = false;
                        break;
                    }
                }
                if (kingSideClear) {
                    // if no white king or white king side rook moves were made
                    if (historyPlays.size() > 0) {
                        if (historyPlays.stream().noneMatch(map -> map.containsKey('K')) ||
                                historyPlays.stream().noneMatch(map -> map.get('R').getSourceCol() == 7)) {
                            if (displayBoard[0][7] == 'R' && !hasWhiteKingCastled) {
                                ZMove move = new ZMove(row, col, row, col + 2);
                                move.setMoveLabel(ZMoveLabel.KING_SIDE_CASTLE);
                                moves.add(move);
                                canWhiteKingCastle = true;
                            }
                        }
                    }
                }

                // queen side castle
                for (int i = 1; i < 4; i++) {
                    if (!isEmpty(row, col - i) || blackThreateningTiles[row][col - i]) {
                        queenSideClear = false;
                        break;
                    }
                }
                if (queenSideClear) {
                    if (historyPlays.size() > 0) {
                        // if no white king or white queen side rook moves were made
                        if (historyPlays.stream().noneMatch(map -> map.containsKey('K')) ||
                                historyPlays.stream().noneMatch(map -> map.get('R').getSourceCol() == 0)) {
                            if (displayBoard[0][0] == 'R' && !hasWhiteQueenCastled) {
                                ZMove move = new ZMove(row, col, row, col - 2);
                                move.setMoveLabel(ZMoveLabel.QUEEN_SIDE_CASTLE);
                                moves.add(move);
                                canWhiteQueenCastle = true;
                            }
                        }
                    }
                }
            }
            // black castling
        } else if (isKingBlack) {
            if (row == 7 && col == 4) {
                // king side castle
                for (int i = 1; i < 3; i++) {
                    // if tiles between king and rooks are empty and aren't threatened
                    if (!isEmpty(row, col + i) || whiteThreateningTiles[row][col + i]) {
                        kingSideClear = false;
                        break;
                    }
                }
                if (kingSideClear) {
                    if (historyPlays.size() > 0) {
                        // if no black king or black king side black rook moves were made
                        if (historyPlays.stream().noneMatch(map -> map.containsKey('k')) ||
                                historyPlays.stream().noneMatch(map -> map.get('r').getSourceCol() == 7)) {
                            if (displayBoard[7][7] == 'r' && !hasBlackKingCastled) {
                                // king side castling
                                ZMove move = new ZMove(row, col, row, col - 2);
                                move.setMoveLabel(ZMoveLabel.KING_SIDE_CASTLE);
                                moves.add(move);
                                canBlackKingCastle = true;
                            }
                        }
                    }
                }

                // queen side castle
                for (int i = 1; i < 4; i++) {
                    if (!isEmpty(row, col - i) || whiteThreateningTiles[row][col - i]) {
                        queenSideClear = false;
                        break;
                    }
                }
                if (queenSideClear) {
                    if (historyPlays.size() > 0) {
                        // if no black king or black queen side black rook moves were made
                        if (historyPlays.stream().noneMatch(map -> map.containsKey('k')) ||
                                historyPlays.stream().noneMatch(map -> map.get('r').getSourceCol() == 0)) {
                            if (displayBoard[7][0] == 'r' && !hasBlackQueenCastled) {
                                ZMove move = new ZMove(row, col, row, col - 2);
                                move.setMoveLabel(ZMoveLabel.QUEEN_SIDE_CASTLE);
                                moves.add(move);
                                canBlackQueenCastle = true;
                            }
                        }
                    }
                }
            }
        }

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
                    move.setMoveLabel(ZMoveLabel.REGULAR);
                    moves.add(move);
                    whiteThreateningTiles[row + r][col + c] = true;
                } else if (isWhitePiece(displayBoard[row + r][col + c])) {
                    whiteThreateningTiles[row + r][col + c] = true;
                    continue;
                } else if (isBlackPiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    move.setMoveLabel(ZMoveLabel.CAPTURE);
                    move.setCapturedPiece(displayBoard[row + r][col + c]);
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
                    move.setMoveLabel(ZMoveLabel.REGULAR);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                } else if (isWhitePiece(displayBoard[row + r][col + c])) {
                    ZMove move = new ZMove(row, col, row + r, col + c);
                    move.setMoveLabel(ZMoveLabel.CAPTURE);
                    move.setCapturedPiece(displayBoard[row + r][col + c]);
                    moves.add(move);
                    blackThreateningTiles[row + r][col + c] = true;
                    continue;
                } else if (isBlackPiece(displayBoard[row + r][col + c])) {
                    blackThreateningTiles[row + r][col + c] = true;
                }
            }
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

    public boolean isGameOver() {
        // checkmate
        return isPlayerInCheck(currentPlayer) && generateMoves().size() == 0;
    }

    public boolean isPlayerInCheck(int player) {
        if (player == WHITE_PLAYER) {
            return blackThreateningTiles[wKingLocation[0]][wKingLocation[1]];
        } else return whiteThreateningTiles[bKingLocation[0]][bKingLocation[1]];
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
                System.out.print(displayBoard[r][c] + "  ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public char[][] getDisplayBoard() {
        return displayBoard;
    }

    public void setDisplayBoard(char[][] board) {
        zobristHash = 0L;
        displayBoard = board;

        for (int pieceType = 0; pieceType < 12; pieceType++) {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    this.board[pieceType][row][col] = random.nextLong();
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

        // setting initial pieces' positions in board to their respective locations' arrays
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
                            wQueenLocations[0][0] = row;
                            wQueenLocations[0][1] = col;
                        }
                        case 'K' -> {
                            wKingLocation[0] = row;
                            wKingLocation[1] = col;
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
                            bQueenLocations[0][0] = row;
                            bQueenLocations[0][1] = col;
                        }
                        case 'k' -> {
                            bKingLocation[0] = row;
                            bKingLocation[1] = col;
                        }
                    }
                }
            }
        }

        currentPlayer = WHITE_PLAYER;

        calculateZobristHash();
        transpositionTable.put(zobristHash, this.board);

        System.out.println(zobristHash + "\n");
    }

    public void setCurrentPlayer(int player) {
        currentPlayer = player;
    }

    public long getZobristHash() {
        return zobristHash;
    }

    public double evaluateBoard() {
        double score = 0.0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (!isEmpty(row, col)) {
                    char piece = displayBoard[row][col];
                    switch (piece) {
                        case 'P' -> {
                            score += 10;
                            if (blackThreateningTiles[row][col]) score -= 0.12 * 10;
                            score += wPawnStrongTiles[row][col];
                        }
                        case 'N' -> {
                            score += 30;
                            if (blackThreateningTiles[row][col]) score -= 0.12 * 30;
                            score += wKnightStrongTiles[row][col];
                        }
                        case 'B' -> {
                            score += 30;
                            if (blackThreateningTiles[row][col]) score -= 0.12 * 30;
                            score += wBishopStrongTiles[row][col];
                        }
                        case 'R' -> {
                            score += 50;
                            if (blackThreateningTiles[row][col]) score -= 0.12 * 50;
                            score += wRookStrongTiles[row][col];
                        }
                        case 'Q' -> {
                            score += 90;
                            if (blackThreateningTiles[row][col]) score -= 0.12 * 90;
                            score += wQueenStrongTiles[row][col];
                        }
                        case 'K' -> {
                            // irrelevant but adding it for debugging purposes
                            score += 900;
                            if (blackThreateningTiles[row][col]) score += 0.12 * 900;
                            // relevant for board evaluation
                            score += wKingStrongTiles[row][col];
                        }
                        case 'p' -> {
                            score -= 10;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 10;
                            score += bPawnStrongTiles[row][col];
                        }
                        case 'n' -> {
                            score -= 30;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 30;
                            score += bKnightStrongTiles[row][col];
                        }
                        case 'b' -> {
                            score -= 30;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 30;
                            score += bBishopStrongTiles[row][col];
                        }
                        case 'r' -> {
                            score -= 50;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 50;
                            score += bRookStrongTiles[row][col];
                        }
                        case 'q' -> {
                            score -= 90;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 90;
                            score += bQueenStrongTiles[row][col];
                        }
                        case 'k' -> {
                            // irrelevant but adding it for debugging purposes
                            score -= 900;
                            if (whiteThreateningTiles[row][col]) score -= 0.12 * 900;
                            // relevant for board evaluation
                            score += bKingStrongTiles[row][col];
                        }
                    }
                }
            }
        }

        if (canWhiteKingCastle || canWhiteQueenCastle) score += 30;
        if (canBlackKingCastle || canBlackQueenCastle) score -= 30;

        if (hasWhiteKingCastled) {
            if (!addedScoreForWK) {
                score += 70;
                addedScoreForWK = true;
            }
        }
        if (hasWhiteQueenCastled) {
            if (!addedScoreForWQ) {
                score += 65;
                addedScoreForWQ = true;
            }
        }
        if (hasBlackKingCastled) {
            if (!addedScoreForBK) {
                score -= 70;
                addedScoreForBK = true;
            }
        }
        if (hasBlackQueenCastled) {
            if (!addedScoreForBQ) {
                score -= 65;
                addedScoreForBQ = true;
            }
        }

        if (isPlayerInCheck(WHITE_PLAYER)) score -= 35;
        else if (isPlayerInCheck(BLACK_PLAYER)) score += 35;

        return score;
    }


    private static double[][] revertStrongTiles(double[][] tiles) {
        double[][] newTiles = new double[8][8];

        int rowIndex = 0;

        for (int r = tiles.length - 1; r >= 0; r--) {
            for (int c = 0; c < tiles.length; c++) {
                newTiles[rowIndex][c] = -1 * tiles[r][c];
            }
            rowIndex++;
        }
        return newTiles;
    }
}
