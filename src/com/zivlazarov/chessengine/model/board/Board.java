package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.controllers.PlayerController;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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
    private final Map<String, Piece> blackAlivePieces;
    private final Map<String, Piece> whiteAlivePieces;
    private final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();
    private final Map<PieceColor, GameSituation> checkmateSituations = new HashMap<>();
    private Tile[][] board;
    private transient Player whitePlayer;
    private transient Player blackPlayer;
    private GameSituation gameSituation;
    private List<MyObserver> observers;

    private final Stack<Pair<Piece, Pair<Tile, Tile>>> gameHistoryMoves;

    private boolean changedState;

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

    private Board() {
        board = new Tile[8][8];
        blackAlivePieces = new HashMap<>();
        whiteAlivePieces = new HashMap<>();

        observers = new ArrayList<>();

        gameHistoryMoves = new Stack<>();

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

    public void refreshPiecesOfPlayer(Player player) {
        player.getLegalMoves().clear();
        player.getPiecesCanMove().clear();
        for (Piece piece : player.getAlivePieces()) {
            piece.refresh();
            player.getLegalMoves().addAll(piece.getPossibleMoves());
            if (piece.canMove()) player.getPiecesCanMove().add(piece);
        }
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
        } else {
            gameSituation = GameSituation.NORMAL;
        }
    }

    public void generateLegalMovesWhenInCheck(Player currentPlayer) {
        List<Tile> pseudoLegalMoves = currentPlayer.getLegalMoves();
        List<Pair<Piece, Tile>> actualLegalMoves = new ArrayList<>();

        for (Piece piece : currentPlayer.getAlivePieces()) {
            saveState();
            synchronized (piece) {
                for (Tile tile : pseudoLegalMoves.stream().filter(
                        tile -> piece.getPossibleMoves().contains(tile)).collect(Collectors.toList())) {
                    if (!makeMove(currentPlayer, piece, tile)) {
                        setChanged();
                        updateObservers();
                        clearChanged();
                        continue;
                    }
                    if (!currentPlayer.isInCheck()) {
                        actualLegalMoves.add(new Pair<>(piece, tile));
                    }
//                    unmakeLastMove(piece);
                    board = loadState().getBoard();
                }
            }
        }
        if (actualLegalMoves.size() == 0) gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
        currentPlayer.getLegalMoves().clear();
        for (Pair<Piece, Tile> pair : actualLegalMoves) {
            currentPlayer.getLegalMovesForPiece().add(pair);
        }
        // checking if any of current piece's tiles to move to contains player's legal moves
    }

    // save board state before making the move for being able to restore it later on!
    public boolean makeMove(Player player, Piece piece, Tile tile) {
        if (piece.isAlive()) return false;
        if (!player.getPiecesCanMove().contains(piece)) return false;
        if (piece.getPossibleMoves().contains(tile)) {
            piece.getCurrentTile().setPiece(null);
            if (!tile.isEmpty() && tile.getPiece().getPieceColor() != piece.getPieceColor()) {
                player.getOpponentPlayer().addPieceToDead(tile.getPiece());
            }
        } else return false;

        // pawn logic for en passant
        if (piece instanceof PawnPiece) {
            if (((PawnPiece) piece).getEnPassantTile() != null) {
                if (tile.equals(((PawnPiece) piece).getEnPassantTile())) {
                    player.getOpponentPlayer().addPieceToDead(
                            board[((PawnPiece)piece).getEnPassantTile().getRow()-player.getPlayerDirection()]
                                    [((PawnPiece)piece).getEnPassantTile().getCol()]
                                    .getPiece());
                }
            }
            ((PawnPiece) piece).setHasMoved(true);
            if (piece.getCurrentTile().getRow() == player.getPlayerDirection() * (board.length - 1)) {
                // setting it as dead and adding it to deadPieces list
                piece.setIsAlive(false);
                player.addPieceToDead(piece);
                Tile targetTile = piece.getCurrentTile();
                Piece convertedPiece = null;
                // clearing piece from it's tile to set a new piece
                targetTile.setPiece(null);

                char chosenPiece = PlayerController.receivePawnPromotionChoice();

                switch (chosenPiece) {
                    case 'q' -> convertedPiece = new QueenPiece(player, this, player.getPlayerColor(), targetTile);
                    case 'b' -> convertedPiece = new BishopPiece(player, this, player.getPlayerColor(), targetTile, player.getNumOfBishops() + 1);
                    case 'n' -> convertedPiece = new KnightPiece(player, this, player.getPlayerColor(), targetTile, player.getNumOfKnights() + 1);
                    case 'r' -> convertedPiece = new RookPiece(player, this, player.getPlayerColor(), targetTile, false, player.getNumOfRooks() + 1);

                }
                if (convertedPiece != null) player.addPieceToAlive(convertedPiece);
            }
        }

        // king logic for castling
        if (piece instanceof KingPiece) {
            if (!piece.hasMoved() && tile.equals(((KingPiece) piece).getKingSideCastleTile())
            && getKingSideRookPiece(player) != null
            && !getKingSideRookPiece(player).hasMoved()) {
//                player.kingSideCastle((KingPiece) piece, (RookPiece) ((KingPiece) piece).getKingSideCastleTile().getPiece());
                // logging rook move
                RookPiece kingSideRookPiece = getKingSideRookPiece(player);
                kingSideRookPiece.getHistoryMoves().push(new Pair<Tile, Tile>(
                        kingSideRookPiece.getCurrentTile(),
                        kingSideRookPiece.getKingSideCastlingTile()));
                // setting rook tile to it's king side castling tile
                kingSideRookPiece.getCurrentTile().setPiece(null);
                kingSideRookPiece.setCurrentTile(kingSideRookPiece.getKingSideCastlingTile());
                kingSideRookPiece.setHasMoved(true);
                // adding castling to game history moves
                gameHistoryMoves.push(new Pair<>(kingSideRookPiece, kingSideRookPiece.getLastMove()));

            } else if (!piece.hasMoved() && tile.equals(((KingPiece) piece).getQueenSideCastleTile())
                    && getQueenSideRookPiece(player) != null
                    && !getQueenSideRookPiece(player).hasMoved()) {
//                player.queenSideCastle((KingPiece) piece, (RookPiece) ((KingPiece) piece).getQueenSideCastleTile().getPiece());
                // logging rook move
                RookPiece queenSideRookPiece = getQueenSideRookPiece(player);
                queenSideRookPiece.getHistoryMoves().push(new Pair<>(
                                queenSideRookPiece.getCurrentTile(),
                        queenSideRookPiece.getQueenSideCastlingTile()));
                // setting rook tile to it's queen side castling tile
                queenSideRookPiece.getCurrentTile().setPiece(null);
                queenSideRookPiece.setCurrentTile(queenSideRookPiece.getQueenSideCastlingTile());
                queenSideRookPiece.setHasMoved(true);
                // adding castling to game history moves
                gameHistoryMoves.push(new Pair<>(queenSideRookPiece, queenSideRookPiece.getLastMove()));
            }
            ((KingPiece) piece).setHasMoved(true);
            // letting the chosen piece (king) to update it's location on board and everything else
        }

        piece.getHistoryMoves().push(new Pair<Tile, Tile>(piece.getCurrentTile(), tile));
        piece.setCurrentTile(tile);
//        historyMoves.push(new Pair<Piece, Pair<Tile, Tile>>(piece, new Pair<Tile, Tile>(piece.getCurrentTile(), tile)));
        gameHistoryMoves.push(new Pair<>(piece, piece.getLastMove()));
        setChanged();
        updateObservers();
        clearChanged();
        return true;
    }

    public void unmakeLastMove(Piece piece) {
        if (gameHistoryMoves.size() == 0) return;
        if (!gameHistoryMoves.lastElement().getFirst().equals(piece)) return;
        Tile previousTile = gameHistoryMoves.lastElement().getSecond().getFirst();
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

    public Stack<Pair<Piece, Pair<Tile, Tile>>> getGameHistoryMoves() {
        return gameHistoryMoves;
    }

    public RookPiece getKingSideRookPiece(Player player) {
        for (Piece piece : player.getAlivePieces()) {
            if (piece instanceof RookPiece) {
                if (((RookPiece) piece).isKingSide()) return (RookPiece) piece;
            }
        }
        return null;
    }

    public RookPiece getQueenSideRookPiece(Player player) {
        for (Piece piece : player.getAlivePieces()) {
            if (piece instanceof RookPiece) {
                if (((RookPiece) piece).isQueenSide()) return (RookPiece) piece;
            }
        }
        return null;
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

    @Override
    public void updateObservers() {
        for (MyObserver observer : observers) {
            observer.update();
        }
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
