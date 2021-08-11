package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.io.*;
import java.util.*;

// make as singleton (?)
public class Board implements MyObservable, Serializable {

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
    private final Tile[][] board;

    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;

    private final Map<Player, KingPiece> kingsMap;
    private final Map<Player, RookPiece> kingSideRooksMap;
    private final Map<Player, RookPiece> queenSideRooksMap;

    private GameSituation gameSituation;
    private boolean canContinueGame;

    private final List<MyObserver> observers;

    private final Stack<Pair<Piece, Tile>> gameHistoryMoves;

    private final Stack<Move> matchPlays;

    private boolean changedState;

    private final Stack<Board> states;

    public Board() {
        board = new Tile[8][8];

        observers = new ArrayList<>();

        gameHistoryMoves = new Stack<>();

        matchPlays = new Stack<>();

        states = new Stack<>();

        kingsMap = new HashMap<>();
        kingSideRooksMap = new HashMap<>();
        queenSideRooksMap = new HashMap<>();

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
        canContinueGame = true;
        gameSituation = GameSituation.NORMAL;
    }

    public void initBoard() {
        Piece whiteRookKingSide = new RookPiece(whitePlayer, this, board[0][7], 0);
        Piece whiteRookQueenSide = new RookPiece(whitePlayer, this, board[0][0], 1);

        Piece blackRookQueenSide = new RookPiece(blackPlayer, this, board[7][0], 0);
        Piece blackRookKingSide = new RookPiece(blackPlayer, this, board[7][7], 1);

        Piece whiteKnightKingSide = new KnightPiece(whitePlayer, this, board[0][1], 0);
        Piece whiteKnightQueenSide = new KnightPiece(whitePlayer, this, board[0][6], 1);

        Piece blackKnightKingSide = new KnightPiece(blackPlayer, this, board[7][1], 0);
        Piece blackKnightQueenSide = new KnightPiece(blackPlayer, this, board[7][6], 1);

        Piece whiteBishopKingSide = new BishopPiece(whitePlayer, this, board[0][2], 0);
        Piece whiteBishopQueenSide = new BishopPiece(whitePlayer, this, board[0][5], 1);

        Piece blackBishopKingSide = new BishopPiece(blackPlayer, this, board[7][5], 0);
        Piece blackBishopQueenSide = new BishopPiece(blackPlayer, this, board[7][2], 1);

        Piece whiteQueen = new QueenPiece(whitePlayer, this, board[0][3]);
        Piece blackQueen = new QueenPiece(blackPlayer, this, board[7][3]);

        Piece whitePawn0 = new PawnPiece(whitePlayer, this, board[1][0], 0);
        Piece whitePawn1 = new PawnPiece(whitePlayer, this, board[1][1], 1);
        Piece whitePawn2 = new PawnPiece(whitePlayer, this, board[1][2], 2);
        Piece whitePawn3 = new PawnPiece(whitePlayer, this, board[1][3], 3);
        Piece whitePawn4 = new PawnPiece(whitePlayer, this, board[1][4], 4);
        Piece whitePawn5 = new PawnPiece(whitePlayer, this, board[1][5], 5);
        Piece whitePawn6 = new PawnPiece(whitePlayer, this, board[1][6], 6);
        Piece whitePawn7 = new PawnPiece(whitePlayer, this, board[1][7], 7);

        Piece blackPawn0 = new PawnPiece(blackPlayer, this, board[6][0], 0);
        Piece blackPawn1 = new PawnPiece(blackPlayer, this, board[6][1], 1);
        Piece blackPawn2 = new PawnPiece(blackPlayer, this, board[6][2], 2);
        Piece blackPawn3 = new PawnPiece(blackPlayer, this, board[6][3], 3);
        Piece blackPawn4 = new PawnPiece(blackPlayer, this, board[6][4], 4);
        Piece blackPawn5 = new PawnPiece(blackPlayer, this, board[6][5], 5);
        Piece blackPawn6 = new PawnPiece(blackPlayer, this, board[6][6], 6);
        Piece blackPawn7 = new PawnPiece(blackPlayer, this, board[6][7], 7);

        Piece whiteKing = new KingPiece(whitePlayer, this, board[0][4]);
        Piece blackKing = new KingPiece(blackPlayer, this, board[7][4]);
    }

    public void checkBoard() {
        // resetting tiles threatened state before every board check
        resetThreatsOnTiles();
        // update all observers
        currentPlayer.getOpponentPlayer().update();
        currentPlayer.update();
        // for each player's legal move's target piece, that piece is in danger of being eaten
//        markEndangeredPiecesFromPlayer(currentPlayer);
//        markEndangeredPiecesFromPlayer(currentPlayer.getOpponentPlayer());

        // checking for specific stalemate conditions
        if (currentPlayer.getAlivePieces().size() == 2 && currentPlayer.getOpponentPlayer().getAlivePieces().size() == 2) {
            Piece king = currentPlayer.getKing();
            int kingIndex = currentPlayer.getAlivePieces().indexOf(king);
            int pieceIndex = kingIndex == 0 ? 1 : 0;
            Piece piece = currentPlayer.getAlivePieces().get(pieceIndex);

            if (piece instanceof BishopPiece) {
                gameSituation = GameSituation.DRAW;
                canContinueGame = false;
                return;
            }

        } else if (currentPlayer.getAlivePieces().size() == 1 && currentPlayer.getOpponentPlayer().getAlivePieces().size() == 1) {
            gameSituation = GameSituation.DRAW;
            canContinueGame = false;
            return;
        }

        if (currentPlayer.isInCheck()) {
            // reset all legal moves before proceeding to generation of legal moves in check situation
            gameSituation = checkSituations.get(currentPlayer.getPlayerColor());
            gameSituation = generateMovesWhenInCheck(currentPlayer);
            return;

        } else if (!currentPlayer.isInCheck() && currentPlayer.getMoves().size() == 0) {
            gameSituation = GameSituation.STALEMATE;
            canContinueGame = false;
            return;

        } else {
            if (gameSituation == GameSituation.NORMAL) {
                Map<Piece, List<Tile>> directionsOfDanger = calculatePotentialDangerForKing(currentPlayer);

                for (Piece opponentPiece : directionsOfDanger.keySet()) {
                    if (opponentPiece != null) {
                        // in each direction of threat, count how many pieces are in between king and threatening piece
                        List<Piece> playerPiecesInTheWay = new ArrayList<>();
                        for (Tile tile : directionsOfDanger.get(opponentPiece)) {
                            if (!tile.isEmpty()) {
                                if (tile.getPiece().getPieceColor() == currentPlayer.getPlayerColor()) {
                                    Piece playerPiece = tile.getPiece();

                                    playerPiecesInTheWay.add(playerPiece);
                                } else {
                                    currentPlayer.getKing().getMoves().removeIf(move -> move.getTargetTile().equals(tile));
                                    currentPlayer.getKing().getPossibleMoves().removeIf(t -> t.equals(tile));
                                    currentPlayer.getMoves().removeIf(move -> move.getMovingPiece().equals(currentPlayer.getKing())
                                    && move.getTargetTile().equals(tile));
                                }
                            }
                        }
                        // if there's only one player's piece in the way between king and opponent threatening piece, limit it's moves, else not
                        if (playerPiecesInTheWay.size() == 1) {
                            Piece playerPiece = playerPiecesInTheWay.get(0);

                            currentPlayer.getMoves().removeIf(move -> move.getMovingPiece().equals(playerPiece));
                            if (directionsOfDanger.get(opponentPiece).size() > 1) {
                                playerPiece.getPossibleMoves().removeIf(t -> !directionsOfDanger.get(opponentPiece).contains(t));
                                playerPiece.getMoves().removeIf(move -> !directionsOfDanger.get(opponentPiece).contains(
                                        move.getTargetTile()
                                ));
                            } else {
                                playerPiece.getPossibleMoves().removeIf(t -> !t.equals(opponentPiece.getCurrentTile()));
                                playerPiece.getMoves().removeIf(move -> !move.getTargetTile().equals(opponentPiece.getCurrentTile()));
                            }
                            currentPlayer.getMoves().addAll(playerPiece.getMoves());
                        }
                    }
                }

                if (currentPlayer.getMoves().size() == 0) {
                    gameSituation = GameSituation.STALEMATE;
                    return;
                }
            }
        }
        gameSituation = GameSituation.NORMAL;
    }

    public Map<Piece, List<Tile>> calculatePotentialDangerForKing(Player player) {
        // setting potential threatening piece as key of threatened row/column/diagonal
        Map<Piece, List<Tile>> allDirections = new HashMap<>();

        int[][] diagonalDirections = {
                {1, 1},
                {1, -1},
                {-1, -1},
                {-1, 1}
        };

        // horizontal and vertical directions
        int[][] hwDirections = {
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
        };

        Tile kingTile = player.getKing().getCurrentTile();
        int kingTileRow;
        int kingTileCol;

        if (kingTile != null) {
            kingTileRow = kingTile.getRow();
            kingTileCol = kingTile.getCol();
        } else return Map.of();

        // looping through the row and column of the king
        for (int[] directions : hwDirections) {
            int row = directions[0];
            int col = directions[1];

            if (kingTileRow + row > board.length - 1 || kingTileRow + row < 0 ||
            kingTileCol + col > board.length - 1 || kingTileCol + col < 0) continue;

            List<Tile> currentTiles = new ArrayList<>();

            for (int i = 1; i < board.length; i++) {
                if (kingTileRow + row*i > board.length - 1 || kingTileRow + row*i < 0 ||
                kingTileCol + col*i > board.length - 1 || kingTileCol + col*i < 0) continue;

                Tile currentTile =  board[kingTileRow + row*i][kingTileCol + col*i];
                currentTiles.add(currentTile);

                if (!currentTile.isEmpty()) {
                    if (currentTile.getPiece().getPieceColor() != player.getPlayerColor()) {
                        Piece opponentPiece = currentTile.getPiece();
                        if (opponentPiece instanceof QueenPiece || opponentPiece instanceof RookPiece) {
                            // if opponent contains queen or rook, all tiles in this row/column with player's pieces in between king and
                            // queen/rook shouldn't be able to move so the next tiles can't affect, so break
                            allDirections.put(opponentPiece, currentTiles);
                        }
                        break;
                    } else continue;
                }
                if (i == board.length - 1) allDirections.put(null, currentTiles);
            }
        }

        // loop through king's diagonals
        for (int[] diagonalDirection : diagonalDirections) {
            int row = diagonalDirection[0];
            int col = diagonalDirection[1];

            if (kingTileRow + row > board.length - 1 || kingTileRow + row < 0 ||
                    kingTileCol + col > board.length - 1 || kingTileCol + col < 0) continue;

            List<Tile> currentTiles = new ArrayList<>();

            for (int i = 1; i < board.length; i++) {
                if (kingTileRow + row*i > board.length - 1 || kingTileRow + row*i < 0 ||
                kingTileCol + col*i > board.length - 1 || kingTileCol + col*i < 0) continue;

                Tile currentTile =  board[kingTileRow + row*i][kingTileCol + col*i];
                currentTiles.add(currentTile);

                if (!currentTile.isEmpty()) {
                    if (currentTile.getPiece().getPieceColor() != player.getPlayerColor()) {
                        Piece opponentPiece = currentTile.getPiece();
                        if (opponentPiece instanceof QueenPiece || opponentPiece instanceof BishopPiece) {
                            allDirections.put(opponentPiece, currentTiles);
                        }
                        break;
                    } else continue;
                }
                if (i == board.length - 1) allDirections.put(null, currentTiles);
            }
        }

        return allDirections;
    }

    public GameSituation generateMovesWhenInCheck(Player player) {
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
            return checkmateSituations.get(player.getPlayerColor());
        }

//        player.getLegalMoves().clear();
        player.getAlivePieces().forEach(piece -> piece.getPossibleMoves().clear());
        player.getMoves().clear();
        player.getMoves().addAll(actualLegalMoves);

        for (Move move : player.getMoves()) {
            Piece piece = move.getMovingPiece();
            Tile tile = move.getTargetTile();

            piece.getPossibleMoves().add(tile);
        }

        setCurrentPlayer(player);
        return gameSituation;
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
        for (Move move : player.getMoves()) {
            Tile tile = move.getTargetTile();
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

    public boolean isCheckmate() {
        return gameSituation == GameSituation.BLACK_CHECKMATED || gameSituation == GameSituation.WHITE_CHECKMATED;
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

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
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

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        currentPlayer.setIsCurrentPlayer(true);
        currentPlayer.getOpponentPlayer().setIsCurrentPlayer(false);
        this.currentPlayer = currentPlayer;
    }

    public Stack<Move> getMatchPlays() {
        return matchPlays;
    }

    public boolean canContinueGame() {
        return canContinueGame;
    }

    public String getLastMoveToString() {
        if (matchPlays.lastElement() != null) {
            return matchPlays.lastElement().getPlayer() + " played " + matchPlays.lastElement();
        }
        return "No plays played.";
    }

    public Map<Player, KingPiece> getKingsMap() {
        return kingsMap;
    }

    public Map<Player, RookPiece> getKingSideRooksMap() {
        return kingSideRooksMap;
    }

    public Map<Player, RookPiece> getQueenSideRooksMap() {
        return queenSideRooksMap;
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

    @Override
    public String toString() {
        printBoard();
        return "\n";
    }
//    public void generateMovesWhenInCheckNew(Player player) {
//        List<Move> actualLegalMoves = new ArrayList<>();
//
//
//        List<Piece> whitePieces = new ArrayList<>(whitePlayer.getAlivePieces());
//        List<Piece> blackPieces = new ArrayList<>(blackPlayer.getAlivePieces());
//
//        Map<Tile, Piece> tilePieceMap = new HashMap<>();
//
//        for (Piece piece : whitePieces) {
//            tilePieceMap.put(piece.getCurrentTile(), piece);
//        }
//        for (Piece piece : blackPieces) {
//            tilePieceMap.put(piece.getCurrentTile(), piece);
//        }
//
//        for (Move move : new ArrayList<>(player.getMoves())) {
//            boolean successfulMove = move.makeMove(false);
//
//            if (!successfulMove) continue;
//            else updateObservers();
//
//            if (!player.isInCheck()) actualLegalMoves.add(move);
//
//            for (Tile[] tiles : board) {
//                for (Tile tile : tiles) {
//                    if (!tile.isEmpty()) {
//                        tile.setPiece(null);
//                    }
//                    if (tilePieceMap.get(tile) != null) {
//                        tile.setPiece(tilePieceMap.get(tile));
//                    }
//                }
//            }
//            updateObservers();
//        }
//
//        if (actualLegalMoves.size() == 0) {
//            gameSituation = checkmateSituations.get(player.getPlayerColor());
//            return;
//        }
//
////        player.getLegalMoves().clear();
//        player.getAlivePieces().forEach(piece -> piece.getPossibleMoves().clear());
//        player.getMoves().clear();
//        player.getMoves().addAll(actualLegalMoves);
//
//        for (Move move : player.getMoves()) {
//            Piece piece = move.getMovingPiece();
//            Tile tile = move.getTargetTile();
//
//            piece.getPossibleMoves().add(tile);
//        }
//
//        setCurrentPlayer(player);
//    }

}
