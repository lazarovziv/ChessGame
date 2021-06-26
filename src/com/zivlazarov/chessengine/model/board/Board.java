package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Piece;
import com.zivlazarov.chessengine.model.player.Player;

import java.util.*;
import java.util.stream.Collectors;

// make as Singleton (?)
public class Board extends Observable {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final Map<String, Piece> blackAlivePieces;
    private final Map<String, Piece> whiteAlivePieces;
    private final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();
    private final Map<PieceColor, GameSituation> checkmateSituations = new HashMap<>();
    private Tile[][] board;
    private Player whitePlayer;
    private Player blackPlayer;
    private GameSituation gameSituation;
    private List<Observer> observers;

    public Board() {
        board = new Tile[8][8];
        blackAlivePieces = new HashMap<>();
        whiteAlivePieces = new HashMap<>();

        observers = new ArrayList<>();

        checkSituations.put(PieceColor.WHITE, GameSituation.WHITE_IN_CHECK);
        checkSituations.put(PieceColor.BLACK, GameSituation.BLACK_IN_CHECK);

        checkmateSituations.put(PieceColor.WHITE, GameSituation.WHITE_CHECKMATED);
        checkmateSituations.put(PieceColor.BLACK, GameSituation.BLACK_CHECKMATED);

        TileColor[] colors = {TileColor.WHITE, TileColor.BLACK};

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                board[r][c] = new Tile(r, c, colors[(r + c) % colors.length]);
            }
        }
        gameSituation = GameSituation.NORMAL;
    }

    public void refreshPieces(Player currentPlayer) {
        currentPlayer.getOpponentPlayer().getLegalMoves().clear();
        currentPlayer.getLegalMoves().clear();
        currentPlayer.getOpponentPlayer().getPiecesCanMove().clear();
        currentPlayer.getPiecesCanMove().clear();
        for (Piece piece : currentPlayer.getOpponentPlayer().getAlivePieces()) {
            piece.refresh();
            currentPlayer.getOpponentPlayer().getLegalMoves().addAll(piece.getTilesToMoveTo());
            if (piece.canMove()) currentPlayer.getOpponentPlayer().getPiecesCanMove().add(piece);
        }
        for (Piece piece : currentPlayer.getAlivePieces()) {
            piece.refresh();
            currentPlayer.getLegalMoves().addAll(piece.getTilesToMoveTo());
            if (piece.canMove()) currentPlayer.getPiecesCanMove().add(piece);
        }
    }

    public void refreshPiecesOfPlayer(Player player) {
        player.getLegalMoves().clear();
        player.getPiecesCanMove().clear();
        for (Piece piece : player.getAlivePieces()) {
            piece.refresh();
            player.getLegalMoves().addAll(piece.getTilesToMoveTo());
            if (piece.canMove()) player.getPiecesCanMove().add(piece);
        }
    }

    public void checkBoard(Player currentPlayer) {
        // resetting tiles threatened state before every check
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                tile.setThreatenedByColor(currentPlayer.getPlayerColor(), false);
                tile.setThreatenedByColor(currentPlayer.getOpponentPlayer().getPlayerColor(), false);
            }
        }
        refreshPiecesOfPlayer(currentPlayer);
        refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());

        // for each player's legal move's target piece, that piece is in danger of being eaten
        for (Tile tile : currentPlayer.getLegalMoves()) {
            tile.setThreatenedByColor(currentPlayer.getPlayerColor(), true);
            if (!tile.isEmpty() && tile.getPiece().getPieceColor() == currentPlayer.getOpponentPlayer().getPlayerColor()) {
                tile.getPiece().setIsInDanger(true);
            }
        }

        for (Tile tile : currentPlayer.getOpponentPlayer().getLegalMoves()) {
            tile.setThreatenedByColor(currentPlayer.getOpponentPlayer().getPlayerColor(), true);
            if (!tile.isEmpty() && tile.getPiece().getPieceColor() == currentPlayer.getPlayerColor()) {
                tile.getPiece().setIsInDanger(true);
            }
        }

        if (currentPlayer.isInCheck()) {
            // reset all legal moves before proceeding to generation of legal moves in check situation
            currentPlayer.getLegalMoves().clear();
            currentPlayer.getPiecesCanMove().clear();
            gameSituation = checkSituations.get(currentPlayer.getPlayerColor());
            generateLegalMovesWhenInCheck(currentPlayer);
        } else {
            gameSituation = GameSituation.NORMAL;
        }
    }

    public synchronized void generateLegalMovesWhenInCheck(Player currentPlayer) {
        refreshPiecesOfPlayer(currentPlayer);
        List<Tile> pseudoLegalMoves = currentPlayer.getLegalMoves();
        List<Tile> actualLegalMoves = new ArrayList<>();

        for (Piece piece : currentPlayer.getAlivePieces()) {
            Runnable runnable = () -> {
                for (Tile tile : pseudoLegalMoves) {
                    if (piece.getTilesToMoveTo().contains(tile)) {
                        piece.moveToTile(tile);
                        refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
//                    refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
                        if (!currentPlayer.isInCheck()) {
                            actualLegalMoves.add(tile);
                            if (!currentPlayer.getPiecesCanMove().contains(piece)) {
                                currentPlayer.getPiecesCanMove().add(piece);
                            }
                        }
                        unmakeLastMove(piece);
                        refreshPieces(currentPlayer.getOpponentPlayer());
                    }
                }
            };
            new Thread(runnable).start();
//            for (Tile tile : pseudoLegalMoves) {
//                if (piece.getTilesToMoveTo().contains(tile)) {
//                    piece.moveToTile(tile);
//                    refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
////                    refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
//                    if (!currentPlayer.isInCheck()) {
//                        actualLegalMoves.add(tile);
//                        currentPlayer.getPiecesCanMove().add(piece);
//                    }
//                    unmakeLastMove(piece);
//                    refreshPieces(currentPlayer.getOpponentPlayer());
//                }
//            }
        }
        if (actualLegalMoves.size() == 0) gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
        currentPlayer.getLegalMoves().clear();
        currentPlayer.getLegalMoves().addAll(actualLegalMoves);
        // checking if any of current piece's tiles to move to contains player's legal moves
    }

    public void unmakeLastMove(Piece piece) {
        if (piece.getHistoryMoves().size() == 1) return;
        Tile previousTile = piece.getHistoryMoves().get(piece.getHistoryMoves().size() - 1);
        piece.getHistoryMoves().remove(previousTile);

        if (piece.getLastPieceEaten() != null) {
            if (piece.getLastPieceEaten().getHistoryMoves().peek().equals(piece.getCurrentTile())) {
                Piece eatenPiece = piece.getLastPieceEaten();
                piece.getCurrentTile().setPiece(eatenPiece);
                eatenPiece.setIsAlive(true);
                eatenPiece.getPlayer().getOpponentPlayer().addPieceToAlive(eatenPiece);
            }
        }

        piece.getCurrentTile().setPiece(null);
        piece.setCurrentTile(previousTile);
        piece.refresh();
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

    public void removePieceFromBoard(Piece piece) {
        piece.setIsAlive(false);
        piece.setCurrentTile(null);
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

    public GameSituation getGameSituation() {
        return gameSituation;
    }

    public void setGameSituation(GameSituation situation) {
        this.gameSituation = situation;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    @Override
    public synchronized void addObserver(Observer o) {
        super.addObserver(o);
        observers.add(o);
    }

    @Override
    public synchronized void deleteObserver(Observer o) {
        super.deleteObserver(o);
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
        for (Observer observer : observers) {
            observer.notify();
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
