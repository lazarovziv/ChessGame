package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Memento;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.io.*;
import java.util.*;

// make as Singleton (?)
public class Board implements MyObservable, Serializable {

    private static volatile Board instance;

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    private final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();
    private final Map<PieceColor, GameSituation> checkmateSituations = new HashMap<>();
    private Tile[][] board;

    private Player whitePlayer;
    private Player blackPlayer;

    private GameSituation gameSituation;
    private List<MyObserver> observers;

    private final Stack<Pair<Piece, Tile>> gameHistoryMoves;

    private boolean changedState;

    private final Stack<Board> states;

    private Board() {
        board = new Tile[8][8];

        observers = new ArrayList<>();

        gameHistoryMoves = new Stack<>();

        states = new Stack<>();

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

    public static Board getInstance() {
        if (instance == null) {
            synchronized (Board.class) {
                if (instance == null) {
                    instance = new Board();
                }
            }
        }
        return instance;
    }

    public void checkBoard(Player currentPlayer) {
        // resetting tiles threatened state before every check
        resetThreatsOnTiles();
        // update all observers
        updateObservers();
        // for each player's legal move's target piece, that piece is in danger of being eaten
        markEndangeredPiecesFromPlayer(currentPlayer);
        markEndangeredPiecesFromPlayer(currentPlayer.getOpponentPlayer());

        if (currentPlayer.isInCheck()) {
            // reset all legal moves before proceeding to generation of legal moves in check situation
            gameSituation = checkSituations.get(currentPlayer.getPlayerColor());
            generateLegalMovesWhenInCheck(currentPlayer);
            return;
        } else {
            if (gameSituation == GameSituation.NORMAL) {
                return;
            } // TODO: add stalemate and draw situations here
        }
        gameSituation = GameSituation.NORMAL;
    }

    public void generateLegalMovesWhenInCheck(Player currentPlayer) {
        Map<Piece, List<Tile>> actualLegalMoves = new HashMap<>();

        // saving players' states
//        saveState();
//        currentPlayer.saveState();
//        opponentPlayer.saveState();

        for (Piece piece : currentPlayer.getAlivePieces()) {
            if (!piece.canMove()) continue;
            List<Tile> potentialLegalMovesForPiece = new ArrayList<>();
            synchronized (piece) {
                for (Tile tile : new ArrayList<>(piece.getPossibleMoves())) {
                    boolean successfulMove = currentPlayer.movePiece(piece, tile);
                    if (!successfulMove) {
                        continue;
                    } else {
                        updateObserver(currentPlayer.getOpponentPlayer());
                    }
                    // if the move broke the check, it's legal
                    if (!currentPlayer.isInCheck()) {
                        potentialLegalMovesForPiece.add(tile);
                    }

                    // unmaking last move
                    currentPlayer.undoLastMove();
                    updateObserver(currentPlayer.getOpponentPlayer());
                }
            }
            // adding for looped piece it's potential legal moves after checking
            actualLegalMoves.put(piece, potentialLegalMovesForPiece);
        }

        int emptyListsCounter = 0;
        for (Piece piece : currentPlayer.getAlivePieces()) {
            if (!actualLegalMoves.containsKey(piece)) continue;
            for (int i = 0; i < actualLegalMoves.size(); i++) {
                if (actualLegalMoves.get(piece).size() == 0) {
                    emptyListsCounter++;
                }
                if (i == actualLegalMoves.size() - 1) {
                    if (emptyListsCounter == actualLegalMoves.size()) {
                        gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
                        return;
                    }
                }
            }
        }
//        if (actualLegalMoves.size() == 0) {
//            gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
//            return;
//        }
        currentPlayer.getLegalMoves().clear();
        // adding each possible move for piece
        for (Piece piece : actualLegalMoves.keySet()) {
            piece.getPossibleMoves().addAll(actualLegalMoves.get(piece));
        }

//        for (Piece piece : currentPlayer.getAlivePieces()) {
//            synchronized (piece) {
//                for (Tile tile : pseudoLegalMoves.stream().filter(
//                        t -> piece.getPossibleMoves().contains(t)).collect(Collectors.toList())) {
//                    if (currentPlayer.movePiece(piece, tile)) {
//                        setChanged();
//                        updateObserver(currentPlayer.getOpponentPlayer());
//                        clearChanged();
//                    } else continue;
//                    if (!currentPlayer.isInCheck()) {
//                        actualLegalMoves.add(tile);
//                    }
////                    unmakeLastMove(piece);
//                    instance = loadState();
//                    currentPlayer = currentPlayer.loadState();
//                    currentPlayer.setOpponentPlayer(currentPlayer.getOpponentPlayer().loadState());
//                }
//            }
//        }
//        // after checking all possible moves, if none is "helping" it means checkmate
//        if (actualLegalMoves.size() == 0) {
//            gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
//            return;
//        }
//        currentPlayer.getLegalMoves().clear();
//        for (Tile tile : actualLegalMoves) {
//            currentPlayer.getLegalMoves().add(tile);
//        }
        // checking if any of current piece's tiles to move to contains player's legal moves
    }

    public void unmakeLastMove(Piece piece) {
        if (gameHistoryMoves.size() == 0) return;
        if (!gameHistoryMoves.lastElement().getFirst().equals(piece)) return;
        Tile previousTile = gameHistoryMoves.lastElement().getSecond();
//        piece.getHistoryMoves().remove(previousTile);

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

        setChanged();
        updateObservers();
        clearChanged();
    }

    public void resetThreatsOnTiles() {
        for (Tile[] tiles : board) {
            for (Tile tile : tiles) {
                tile.setThreatenedByWhite(false);
                tile.setThreatenedByBlack(false);
            }
        }
    }

    public void markEndangeredPiecesFromPlayer(Player player) {
        for (Tile tile : player.getLegalMoves()) {
            tile.setThreatenedByColor(player.getPlayerColor(), true);
        }
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

    public Stack<Pair<Piece, Tile>> getGameHistoryMoves() {
        return gameHistoryMoves;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void saveState() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("board.txt");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Board loadState() {
        Board loadedBoard = null;
        try {
            FileInputStream fileInputStream = new FileInputStream("board.txt");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            loadedBoard = (Board) objectInputStream.readObject();
            objectInputStream.close();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return loadedBoard;
    }

    public Memento<Board> saveToMemento() {
        return new Memento<Board>(instance);
    }

    public void restoreFromMemento(Memento<Board> memento) {
        instance = memento.getSavedState();
    }

    @Override
    public void updateObservers() {
        for (MyObserver observer : observers) {
            observer.update();
        }
        states.add(this);
    }

    @Override
    public void updateObserver(MyObserver observer) {
        observer.update();
    }

    @Override
    public void addObserver(MyObserver observer) {
        observers.add(observer);
    }

    @Override
    public void addAllObservers(MyObserver[] o) {
        observers.addAll(Arrays.asList(o));
    }

    @Override
    public void removeObserver(MyObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void setChanged() {
        changedState = true;
    }

    @Override
    public boolean hasChanged() {
        return changedState;
    }

    @Override
    public void clearChanged() {
        changedState = false;
    }
}
