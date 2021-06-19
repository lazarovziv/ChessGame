package com.zivlazarov.chessengine.model.utils.board;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.utils.Observable;
import com.zivlazarov.chessengine.model.utils.Observer;
import com.zivlazarov.chessengine.model.utils.player.Piece;
import com.zivlazarov.chessengine.model.utils.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// make as Singleton (?)
public class Board implements Observable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private Tile[][] board;

    private final Map<String, Piece> blackAlivePieces;
    private final Map<String, Piece> whiteAlivePieces;
    private GameSituation gameSituation;

    private List<Observer> observers;

    public Board() {
        board = new Tile[8][8];
        blackAlivePieces = new HashMap<>();
        whiteAlivePieces = new HashMap<>();

        observers = new ArrayList<>();

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

    public void checkBoard(Player currentPlayer) {
        // resetting tiles threatened state before every check
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                tile.setThreatenedByWhite(false);
                tile.setThreatenedByBlack(false);
            }
        }
        for (Piece piece : whiteAlivePieces.values()) piece.refresh();
        for (Piece piece : blackAlivePieces.values()) piece.refresh();

        // setting only relevant tiles as threatened
        for (Piece piece : whiteAlivePieces.values()) {
            for (Tile tile : piece.getTilesToMoveTo()) {
                tile.setThreatenedByWhite(true);
                if (!tile.isEmpty() && tile.getPiece().getPieceColor() == PieceColor.BLACK) {
                    tile.getPiece().setIsInDanger(true);
                }
                if (tile.getPiece() == blackAlivePieces.get("bK")) {
                    gameSituation = GameSituation.BLACK_IN_CHECK;
                }
            }
        }
        for (Piece piece : blackAlivePieces.values()) {
            for (Tile tile : piece.getTilesToMoveTo()) {
                tile.setThreatenedByBlack(true);
                if (!tile.isEmpty() && tile.getPiece().getPieceColor() == PieceColor.WHITE) {
                    tile.getPiece().setIsInDanger(true);
                }
                if (tile.getPiece() == whiteAlivePieces.get("wK")) {
                    gameSituation = GameSituation.WHITE_IN_CHECK;
                }
            }
        }
    }

    public Board simulateSituations(Board board, Player currentPlayer, int numOfFutureTurns) {
        if (numOfFutureTurns == 0) return board;
        KingPiece kingPiece = currentPlayer.getKing();
        if (currentPlayer.getPlayerColor() == PieceColor.WHITE) {
            List<Piece> threateningPieces = blackAlivePieces.values().stream().filter(
                    piece -> piece.getPiecesUnderThreat().contains(kingPiece)
            ).collect(Collectors.toList());
            // checking what happens if king eats threatening piece
            for (Piece piece : threateningPieces) {
                if (kingPiece.getPiecesUnderThreat().contains(piece)) {
                    kingPiece.moveToTile(piece.getCurrentTile());
                    board.checkBoard(currentPlayer.getOpponentPlayer());
                    if (board.getGameSituation() == GameSituation.WHITE_IN_CHECK) {
                        return board;
                    }
                }
            }
            // checking if king is being protected

        } else {
            List<Piece> threateningPieces = whiteAlivePieces.values().stream().filter(
                    piece -> piece.getPiecesUnderThreat().contains(kingPiece)
            ).collect(Collectors.toList());
            for (Piece piece : threateningPieces) {
                if (kingPiece.getPiecesUnderThreat().contains(piece)) {
                    kingPiece.moveToTile(piece.getCurrentTile());
                    board.checkBoard(currentPlayer.getOpponentPlayer());
                    if (board.getGameSituation() == GameSituation.BLACK_IN_CHECK) {
                        return board;
                    }
                }
            }
        }
        return board;
    }

    public boolean canPieceGetInTheWayOfPiece(Piece defendingPiece, Piece threateningPiece) {


        return false;
    }

    public int distanceBetweenPieces(Piece first, Piece second) {
        // |x2 - x1| + |y2 - y1| manhattan distance
//        int distance = Math.abs(first.getCurrentTile().getCol() - second.getCurrentTile().getCol()) +
//                Math.abs(first.getCurrentTile().getRow() - second.getCurrentTile().getRow());
        int distance = 0;

        int firstCol = first.getCurrentTile().getCol();
        int firstRow = first.getCurrentTile().getRow();

        int secondCol = second.getCurrentTile().getCol();
        int secondRow = second.getCurrentTile().getRow();

        // same col
        if (Math.abs(firstCol - secondCol) == 0) {
            distance = Math.abs(firstRow - secondRow);
            return distance;
            // same row
        } else if (Math.abs(firstRow - secondRow) == 0) {
            distance = Math.abs(firstCol - secondCol);
            return distance;
            // different col and row
        } else {
            // can be both
            // distance = Math.abs(firstCol - secondCol);
            distance = Math.abs(firstRow - secondRow);
            return distance;
        }
    }

    public boolean canKingEscape(KingPiece kingPiece) {
        return kingPiece.canMove();
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

    public void printBoardUpsideDown() {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = board.length - 1; r >= 0; r--) {
            System.out.println();
            for (int c = board.length - 1; c >= 0; c--) {
                if (c == board.length - 1) System.out.print(nums[r] + " ");
                if (board[r][c].getPiece() != null) {
                    System.out.print(board[r][c].getPiece().getName() + " ");
                } else System.out.print("-- ");
            }
        }
        System.out.println();
        for (int i = nums.length - 1; i >= 0; i--) {
            if (i == nums.length - 1) System.out.print("  ");
            System.out.print(nums[i] + "  ");
        }
        System.out.println();
        System.out.println();
    }

    public void printBoard(Tile tileChosen) {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = 0; r < board.length; r++) {
            System.out.println();
            for (int c = 0; c < board.length; c++) {
                if (c == 0) /*System.out.print(letters[r] + " ");*/ System.out.print(nums[r] + " ");
                if (board[r][c].getPiece() != null) {
                    if (board[r][c] == tileChosen) {
                        System.out.print(ANSI_RED + board[r][c].getPiece().getName() + " " + ANSI_RESET);
                    } else System.out.print(board[r][c].getPiece().getName() + " ");
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

    public void printBoardUpsideDown(Tile tileChosen) {
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = board.length - 1; r >= 0; r--) {
            System.out.println();
            for (int c = board.length - 1; c >= 0; c--) {
                if (c == board.length - 1) /*System.out.print(letters[r] + " ");*/ System.out.print(nums[r] + " ");
                if (board[r][c].getPiece() != null) {
                    if (board[r][c] == tileChosen) {
                        System.out.print(ANSI_RED + board[r][c].getPiece().getName() + " " + ANSI_RESET);
                    } else System.out.print(board[r][c].getPiece().getName() + " ");
                } else System.out.print("-- ");
            }
        }
        System.out.println();
        for (int i = board.length - 1; i >= 0; i--) {
            if (i == board.length - 1) System.out.print("  ");
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

    @Override
    public void updateAll() {

    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }
}
