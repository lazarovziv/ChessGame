package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
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

    private static volatile Board simulatedInstance;

    private BoardNode node;
    private BoardNode currentNode;

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
    private Player currentPlayer;

    private GameSituation gameSituation;
    private List<MyObserver> observers;

    private final Stack<Pair<Piece, Tile>> gameHistoryMoves;

    private final Stack<Move> matchPlays;

    private boolean changedState;

    private final Stack<Board> states;

    private Board() {
        board = new Tile[8][8];

        observers = new ArrayList<>();

        gameHistoryMoves = new Stack<>();

        matchPlays = new Stack<>();

        states = new Stack<>();

        checkSituations.put(PieceColor.WHITE, GameSituation.WHITE_IN_CHECK);
        checkSituations.put(PieceColor.BLACK, GameSituation.BLACK_IN_CHECK);

        checkmateSituations.put(PieceColor.WHITE, GameSituation.WHITE_CHECKMATED);
        checkmateSituations.put(PieceColor.BLACK, GameSituation.BLACK_CHECKMATED);

        TileColor[] colors = {TileColor.WHITE, TileColor.BLACK};

        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                TileColor currentTileColor = colors[(r + c) % colors.length];
                board[r][c] = new Tile(r, c, currentTileColor);
//                simulatedInstance.getBoard()[r][c] = new Tile(r, c, currentTileColor);
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

    public void resetBoard() {
        if (instance != null) {
            synchronized (Board.class) {
                if (instance != null) {
//                    for (Tile[] tiles : board) {
//                        for (Tile tile : tiles) {
//                            tile.setPiece(null);
//                        }
//                    }
                    instance = null;
                }
            }
        }
    }

    public static Board getSimulatedInstance() {
        if (simulatedInstance == null) {
            synchronized (Board.class) {
                if (simulatedInstance == null) simulatedInstance = new Board();
            }
        }
        return simulatedInstance;
    }

    public void initBoard() {
        Piece whiteRookKingSide = new RookPiece(whitePlayer, instance, PieceColor.WHITE, board[0][7], true, 0);
        Piece whiteRookQueenSide = new RookPiece(whitePlayer, instance, PieceColor.WHITE, board[0][0], false, 1);

        Piece blackRookQueenSide = new RookPiece(blackPlayer, instance, PieceColor.BLACK, board[7][0], false, 0);
        Piece blackRookKingSide = new RookPiece(blackPlayer, instance, PieceColor.BLACK, board[7][7], true, 1);

        Piece whiteKnightKingSide = new KnightPiece(whitePlayer, instance, PieceColor.WHITE, board[0][1], 0);
        Piece whiteKnightQueenSide = new KnightPiece(whitePlayer, instance, PieceColor.WHITE, board[0][6], 1);

        Piece blackKnightKingSide = new KnightPiece(blackPlayer, instance, PieceColor.BLACK, board[7][1], 0);
        Piece blackKnightQueenSide = new KnightPiece(blackPlayer, instance, PieceColor.BLACK, board[7][6], 1);

        Piece whiteBishopKingSide = new BishopPiece(whitePlayer, instance, PieceColor.WHITE, board[0][2], 0);
        Piece whiteBishopQueenSide = new BishopPiece(whitePlayer, instance, PieceColor.WHITE, board[0][5], 1);

        Piece blackBishopKingSide = new BishopPiece(blackPlayer, instance, PieceColor.BLACK, board[7][5], 0);
        Piece blackBishopQueenSide = new BishopPiece(blackPlayer, instance, PieceColor.BLACK, board[7][2], 1);

        Piece whiteQueen = new QueenPiece(whitePlayer, instance, PieceColor.WHITE, board[0][3]);
        Piece blackQueen = new QueenPiece(blackPlayer, instance, PieceColor.BLACK, board[7][3]);

        Piece whiteKing = new KingPiece(whitePlayer, instance, PieceColor.WHITE, board[0][4], false);
        Piece blackKing = new KingPiece(blackPlayer, instance, PieceColor.BLACK, board[7][4], false);

        Piece whitePawn0 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][0], 0);
        Piece whitePawn1 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][1], 1);
        Piece whitePawn2 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][2], 2);
        Piece whitePawn3 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][3], 3);
        Piece whitePawn4 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][4], 4);
        Piece whitePawn5 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][5], 5);
        Piece whitePawn6 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][6], 6);
        Piece whitePawn7 = new PawnPiece(whitePlayer, instance, PieceColor.WHITE, board[1][7], 7);

        Piece blackPawn0 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][0], 0);
        Piece blackPawn1 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][1], 1);
        Piece blackPawn2 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][2], 2);
        Piece blackPawn3 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][3], 3);
        Piece blackPawn4 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][4], 4);
        Piece blackPawn5 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][5], 5);
        Piece blackPawn6 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][6], 6);
        Piece blackPawn7 = new PawnPiece(blackPlayer, instance, PieceColor.BLACK, board[6][7], 7);

        node = new BoardNode(instance, whitePlayer);
        currentNode = node;
    }

    public void initChessPieces() {
        ChessPiece whiteRookKingSide = new ChessPiece(whitePlayer, instance, PieceType.ROOK, PieceColor.WHITE, board[0][7]);
        ChessPiece whiteRookQueenSide = new ChessPiece(whitePlayer, instance, PieceType.ROOK, PieceColor.WHITE, board[0][0]);

        ChessPiece blackRookKingSide = new ChessPiece(blackPlayer, instance, PieceType.ROOK, PieceColor.BLACK, board[7][7]);
        ChessPiece blackRookQueenSide = new ChessPiece(blackPlayer, instance, PieceType.ROOK, PieceColor.BLACK, board[7][0]);

        ChessPiece whiteKnightKingSide = new ChessPiece(whitePlayer, instance, PieceType.KNIGHT, PieceColor.WHITE, board[0][6]);
        ChessPiece whiteKnightQueenSide = new ChessPiece(whitePlayer, instance, PieceType.KNIGHT, PieceColor.WHITE, board[0][1]);

        ChessPiece blackKnightKingSide = new ChessPiece(blackPlayer, instance, PieceType.KNIGHT, PieceColor.BLACK, board[7][6]);
        ChessPiece blackKnightQueenSide = new ChessPiece(blackPlayer, instance, PieceType.KNIGHT, PieceColor.BLACK, board[7][1]);

        ChessPiece whiteBishopKingSide = new ChessPiece(whitePlayer, instance, PieceType.BISHOP, PieceColor.WHITE, board[0][5]);
        ChessPiece whiteBishopQueenSide = new ChessPiece(whitePlayer, instance, PieceType.BISHOP, PieceColor.WHITE, board[0][2]);

        ChessPiece blackBishopKingSide = new ChessPiece(blackPlayer, instance, PieceType.BISHOP, PieceColor.BLACK, board[7][5]);
        ChessPiece blackBishopQueenSide = new ChessPiece(blackPlayer, instance, PieceType.BISHOP, PieceColor.BLACK, board[7][2]);

        ChessPiece whiteQueen = new ChessPiece(whitePlayer, instance, PieceType.QUEEN, PieceColor.WHITE, board[0][3]);
        ChessPiece blackQueen = new ChessPiece(blackPlayer, instance, PieceType.QUEEN, PieceColor.BLACK, board[7][3]);

        ChessPiece whiteKing = new ChessPiece(whitePlayer, instance, PieceType.KING, PieceColor.WHITE, board[0][4]);
        ChessPiece blackKing = new ChessPiece(blackPlayer, instance, PieceType.KING, PieceColor.BLACK, board[7][4]);

        ChessPiece whitePawn0 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][0]);
        ChessPiece whitePawn1 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][1]);
        ChessPiece whitePawn2 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][2]);
        ChessPiece whitePawn3 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][3]);
        ChessPiece whitePawn4 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][4]);
        ChessPiece whitePawn5 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][5]);
        ChessPiece whitePawn6 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][6]);
        ChessPiece whitePawn7 = new ChessPiece(whitePlayer, instance, PieceType.PAWN, PieceColor.WHITE, board[1][7]);

        ChessPiece blackPawn0 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][0]);
        ChessPiece blackPawn1 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][1]);
        ChessPiece blackPawn2 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][2]);
        ChessPiece blackPawn3 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][3]);
        ChessPiece blackPawn4 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][4]);
        ChessPiece blackPawn5 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][5]);
        ChessPiece blackPawn6 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][6]);
        ChessPiece blackPawn7 = new ChessPiece(blackPlayer, instance, PieceType.PAWN, PieceColor.BLACK, board[6][7]);

    }

    public void checkBoard(Player currentPlayer) {
        // resetting tiles threatened state before every board check
        resetThreatsOnTiles();
        // update all observers
        updateObservers();
        // for each player's legal move's target piece, that piece is in danger of being eaten
        markEndangeredPiecesFromPlayer(currentPlayer);
        markEndangeredPiecesFromPlayer(currentPlayer.getOpponentPlayer());

        if (currentPlayer.isInCheck()) {
            // reset all legal moves before proceeding to generation of legal moves in check situation
            gameSituation = checkSituations.get(currentPlayer.getPlayerColor());
            generateMovesWhenInCheck(currentPlayer);
            return;
        } else {
            if (gameSituation == GameSituation.NORMAL) {
                if (currentPlayer.getMoves().size() == 0) {
                    gameSituation = GameSituation.STALEMATE;
                    System.out.println("Stalemate! ");
                    return;
                }
            }
        }
        gameSituation = GameSituation.NORMAL;
        
    }

    public void generateMovesWhenInCheck(Player player) {
        List<Move> actualLegalMoves = new ArrayList<>();

        for (Move move : new ArrayList<>(player.getMoves())) {
            // setting checkBoard argument to false to prevent a StackOverflow error
            boolean successfulMove = move.makeMove(false);
            // if the move wasn't successful, try the next move
            if (!successfulMove) continue;
            // if it did, make all pieces generate moves according to current board state
            else updateObservers();

            // if the move prevented the check, it is legal
            if (!player.isInCheck()) actualLegalMoves.add(move);

            // setting checkBoard argument to false to prevent a StackOverflow error
            // unmake the move and make all pieces generate moves
            move.unmakeMove(false);
            updateObservers();
        }

        // if no legal move exists in this current board's state, it is checkmate
        if (actualLegalMoves.size() == 0) {
            gameSituation = checkmateSituations.get(player.getPlayerColor());
            return;
        }

        player.getLegalMoves().clear();
        player.getAlivePieces().forEach(piece -> piece.getPossibleMoves().clear());
        player.getMoves().clear();
        player.getMoves().addAll(actualLegalMoves);

        for (Move move : player.getMoves()) {
            Piece piece = move.getMovingPiece();
            Tile tile = move.getTargetTile();

            piece.getPossibleMoves().add(tile);
        }

        setCurrentPlayer(player);
    }

    public void generateLegalMovesWhenInCheck(Player currentPlayer) {
        Map<Piece, List<Tile>> actualLegalMoves = new HashMap<>();

        ArrayList<Piece> alivePiecesList = new ArrayList<>(currentPlayer.getAlivePieces());
        for (Piece piece : alivePiecesList) {
            if (!piece.canMove()) continue;
            List<Tile> potentialLegalMovesForPiece = new ArrayList<>();
            synchronized (piece) {
                for (Tile tile : new ArrayList<>(piece.getPossibleMoves())) {
                    boolean successfulMove = currentPlayer.movePiece(piece, tile);

                    if (!successfulMove) continue;
                    else updateObservers();

                    // if the move broke the check, it's legal
                    if (!currentPlayer.isInCheck()) {
                        potentialLegalMovesForPiece.add(tile);
                    }

                    // unmaking last move
                    currentPlayer.undoLastMove();
                    updateObservers();
//                    updateObserver(currentPlayer.getOpponentPlayer());
                }
            }
            // adding for looped piece it's potential legal moves after checking
            actualLegalMoves.put(piece, potentialLegalMovesForPiece);
        }

        // start counting for no potential legal moves for every piece
        int emptyListsCounter = 0;

        // iterating on every piece
        for (Piece piece : actualLegalMoves.keySet()) {
            // if it doesn't have any potential moves, increment variable and continue to next piece
            if (actualLegalMoves.get(piece).size() == 0) {
                emptyListsCounter++;
                continue;
            }

            // if all pieces don't have potential legal moves, then it's checkmate, and stop the method
            if (emptyListsCounter == actualLegalMoves.keySet().size()) {
                gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
                System.exit(1);
                return;
            }
        }
//        if (actualLegalMoves.size() == 0) {
//            gameSituation = checkmateSituations.get(currentPlayer.getPlayerColor());
//            return;
//        }
        // for every piece's "normal" legal move, clear it
        currentPlayer.getLegalMoves().clear();
        // adding each possible move for piece
        for (Piece piece : actualLegalMoves.keySet()) {
            piece.getPossibleMoves().clear();
            piece.getPossibleMoves().addAll(actualLegalMoves.get(piece));
        }
        // add all piece's "check" legal moves to player's legal moves list
        currentPlayer.updateLegalMoves();
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
                if (c == 0) /*System.out.print(letters[r] + " ");*/ System.out.print(letters[r] + " ");
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
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = board.length - 1; r >= 0; r--) {
            System.out.println();
            for (int c = board.length - 1; c >= 0; c--) {
                if (c == board.length - 1) System.out.print(letters[r] + " ");
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
                if (c == 0) /*System.out.print(letters[r] + " ");*/ System.out.print(letters[r] + " ");
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
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
        int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

        for (int r = board.length - 1; r >= 0; r--) {
            System.out.println();
            for (int c = board.length - 1; c >= 0; c--) {
                if (c == board.length - 1) /*System.out.print(letters[r] + " ");*/ System.out.print(letters[r] + " ");
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

    public void pushMoveToMatchPlays(Move move) {
        matchPlays.push(move);
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

    public int evaluateBoard(Player player) {
        return player.getPlayerScore() - player.getOpponentPlayer().getPlayerScore();
    }

    public int evaluateBoard() {
        whitePlayer.resetPlayerScore();
        whitePlayer.evaluatePlayerScore();
        blackPlayer.resetPlayerScore();
        blackPlayer.evaluatePlayerScore();

        int perspective = currentPlayer.getPlayerColor() == PieceColor.WHITE ? 1 : -1;

        return currentPlayer.getPlayerScore();
//        return perspective * (currentPlayer.getPlayerScore() - currentPlayer.getOpponentPlayer().getPlayerScore());
    }

    public BoardNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(BoardNode node) {
        currentNode = node;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Stack<Move> getMatchPlays() {
        return matchPlays;
    }

    public String getLastMoveToString() {
        if (matchPlays.lastElement() != null) {
            return matchPlays.lastElement().getPlayer() + " played " + matchPlays.lastElement();
        }
        return "No plays played.";
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

    public boolean isSameBoard(Board other) {
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board.length; c++) {
                Tile tile = board[r][c];
                Tile otherTile = other.getBoard()[r][c];

                if (!tile.isEmpty() && !otherTile.isEmpty()) {
                    if (!tile.getPiece().equals(otherTile.getPiece())) return false;
                }
            }
        }
        return true;
    }
}
