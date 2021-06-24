package com.zivlazarov.chessengine.model.utils.board;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.player.Piece;
import com.zivlazarov.chessengine.model.utils.player.Player;

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

    private Tile[][] board;

    private Player whitePlayer;
    private Player blackPlayer;

    private final Map<String, Piece> blackAlivePieces;
    private final Map<String, Piece> whiteAlivePieces;
    private GameSituation gameSituation;

    private final List<Tile> whiteLegalTilesToMoveToWhenInCheck;
    private final List<Tile> blackLegalTilesToMoveToWhenInCheck;

    private final Map<PieceColor, List<Tile>> legalMovesWhenInCheck;

    private final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();

    private List<Observer> observers;

    public Board() {
        board = new Tile[8][8];
        blackAlivePieces = new HashMap<>();
        whiteAlivePieces = new HashMap<>();

        observers = new ArrayList<>();

        whiteLegalTilesToMoveToWhenInCheck = new ArrayList<>();
        blackLegalTilesToMoveToWhenInCheck = new ArrayList<>();

        legalMovesWhenInCheck = new HashMap<>();

        checkSituations.put(PieceColor.WHITE, GameSituation.WHITE_IN_CHECK);
        checkSituations.put(PieceColor.BLACK, GameSituation.BLACK_IN_CHECK);

        TileColor[] colors = { TileColor.WHITE, TileColor.BLACK };

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                board[r][c] = new Tile(r, c, colors[(r+c) % colors.length]);
            }
        }
        gameSituation = GameSituation.NORMAL;
    }

    public void refreshPieces(Player currentPlayer) {
        currentPlayer.getOpponentPlayer().getLegalMoves().clear();
        currentPlayer.getLegalMoves().clear();
        for (Piece piece : currentPlayer.getOpponentPlayer().getAlivePieces()) {
            piece.refresh();
            currentPlayer.getOpponentPlayer().getLegalMoves().addAll(piece.getTilesToMoveTo());
        }
        for (Piece piece : currentPlayer.getAlivePieces()) {
            piece.refresh();
            currentPlayer.getLegalMoves().addAll(piece.getTilesToMoveTo());
        }
    }

    public void refreshPiecesOfPlayer(Player player) {
        player.getLegalMoves().clear();
        for (Piece piece : player.getAlivePieces()) {
            piece.refresh();
            player.getLegalMoves().addAll(piece.getTilesToMoveTo());
        }
    }

    public void checkBoard(Player currentPlayer) {
        // resetting tiles threatened state before every check
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                tile.setThreatenedByWhite(false);
                tile.setThreatenedByBlack(false);
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

        if (currentPlayer.getKing().getIsInDanger()) {
            legalMovesWhenInCheck.clear();
            gameSituation = checkSituations.get(currentPlayer.getPlayerColor());
            calculateLegalMovesWhenInCheck(currentPlayer);
        } else {
            gameSituation = GameSituation.NORMAL;
        }
    }

    public void calculateLegalMovesWhenInCheck(Player currentPlayer) {
        if (currentPlayer.getPlayerColor() == PieceColor.WHITE) {
            Runnable whiteRunnable = () -> {
                for (Piece piece : currentPlayer.getAlivePieces()) {
                    // problem is in tiles for loop
                    for (Tile tile : piece.getTilesToMoveTo()) {
                        piece.moveToTile(tile);
                        refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
                        List<Piece> piecesThreateningKing = currentPlayer.getOpponentPlayer().getAlivePieces()
                                .stream().filter(p -> p.getPiecesUnderThreat().contains(currentPlayer.getKing()))
                                .collect(Collectors.toList());
                        if (piecesThreateningKing.size() == 0) whiteLegalTilesToMoveToWhenInCheck.add(tile);
                        piece.unmakeLastMove();
                        refreshPiecesOfPlayer(currentPlayer);
                    }
                }
                legalMovesWhenInCheck.put(PieceColor.WHITE, whiteLegalTilesToMoveToWhenInCheck);
                if (legalMovesWhenInCheck.get(PieceColor.WHITE).size() == 0) gameSituation = GameSituation.WHITE_CHECKMATED;
            };
            new Thread(whiteRunnable).start();
        } else if (currentPlayer.getPlayerColor() == PieceColor.BLACK) {
            Runnable blackRunnable = () -> {
                for (Piece piece : currentPlayer.getAlivePieces()) {
                    // problem is in tiles for loop
                    for (Tile tile : piece.getTilesToMoveTo()) {
                        piece.moveToTile(tile);

                        refreshPiecesOfPlayer(currentPlayer.getOpponentPlayer());
                        List<Piece> piecesThreateningKing = currentPlayer.getOpponentPlayer().getAlivePieces()
                                .stream().filter(p -> p.getPiecesUnderThreat().contains(currentPlayer.getKing()))
                                .collect(Collectors.toList());
                        if (piecesThreateningKing.size() == 0) blackLegalTilesToMoveToWhenInCheck.add(tile);
                        piece.unmakeLastMove();
                        refreshPieces(currentPlayer);
                    }
                }
                legalMovesWhenInCheck.put(PieceColor.BLACK, blackLegalTilesToMoveToWhenInCheck);
                if (legalMovesWhenInCheck.get(PieceColor.BLACK).size() == 0) gameSituation = GameSituation.BLACK_CHECKMATED;
            };
            new Thread(blackRunnable).start();
        }
    }

    public void unmakeLastMove(Piece piece) {
//        Stack<Pair<Pair<Player, Piece>, Pair<Tile, Tile>>> log =  MovesLog.getInstance().getMovesLog();
//        Pair<Player, Piece> playerPiecePair = log.peek().getFirst();
//        Pair<Tile, Tile> tilesPair = log.peek().getSecond();
        Piece eatenPiece = piece.lastPieceEaten();
        Pair<Tile, Tile> lastPairOfTiles = piece.getLastMove();

        if (lastPairOfTiles == null || lastPairOfTiles.getFirst() == null || lastPairOfTiles.getSecond() == null) return;

        if (eatenPiece != null) {
            if (lastPairOfTiles.getSecond().equals(eatenPiece.getLastMove().getSecond())) {
                lastPairOfTiles.getFirst().setPiece(piece);
                lastPairOfTiles.getSecond().setPiece(eatenPiece);
                eatenPiece.setIsAlive(true);
            }
        } else {
            lastPairOfTiles.getFirst().setPiece(piece);
            lastPairOfTiles.getSecond().setPiece(null);
        }
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

    public void setGameSituation(GameSituation situation) {
        this.gameSituation = situation;
    }

    public List<Tile> getWhiteLegalTilesToMoveTo() {
        return whiteLegalTilesToMoveToWhenInCheck;
    }

    public List<Tile> getBlackLegalTilesToMoveTo() {
        return blackLegalTilesToMoveToWhenInCheck;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public Map<PieceColor, List<Tile>> getLegalMovesWhenInCheck() {
        return legalMovesWhenInCheck;
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
}
