package com.zivlazarov.chessengine.model.board;

import com.zivlazarov.chessengine.errors.IllegalMoveError;
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
    public static Map<PieceColor, Integer> firstPawnRow = Map.of(
            PieceColor.WHITE, 1,
            PieceColor.BLACK, 6
    );
    private final Map<PieceColor, GameSituation> checkSituations = new HashMap<>();
    private final Map<PieceColor, GameSituation> checkmateSituations = new HashMap<>();
    private Tile[][] board;

    private Player whitePlayer;
    private Player blackPlayer;
    private Player currentPlayer;

    private long zobristHash = 0L;

    private final Map<Player, KingPiece> kingsMap;
    private final Map<Player, RookPiece> kingSideRooksMap;
    private final Map<Player, RookPiece> queenSideRooksMap;

    private GameSituation gameSituation;
    private boolean isGameOver;

    private static final char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H'};
    private static final int[] nums = {1, 2, 3, 4, 5, 6, 7, 8};

    private final List<MyObserver> observers;

    private final Stack<Pair<Piece, Tile>> gameHistoryMoves;

    private final Stack<Move> matchPlays;

    private boolean changedState;

    private final Stack<Board> states;

    private final Stack<Tile[][]> boardStates;

    public Board() {
        board = new Tile[8][8];

        observers = new ArrayList<>();

        gameHistoryMoves = new Stack<>();

        matchPlays = new Stack<>();

        states = new Stack<>();
        boardStates = new Stack<>();

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
            }
        }
        isGameOver = false;
        gameSituation = GameSituation.NORMAL;
    }

    public Board(Board copyBoard) {
        board = copyBoard.getBoard();
        observers = new ArrayList<>();
        gameHistoryMoves = new Stack<>();

        matchPlays = new Stack<>();

        states = new Stack<>();
        boardStates = new Stack<>();

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
            }
        }

        isGameOver = false;

        gameSituation = copyBoard.getGameSituation();

        whitePlayer = new Player(this, copyBoard.getWhitePlayer());
        blackPlayer = new Player(this, copyBoard.getBlackPlayer());
        currentPlayer = copyBoard.getCurrentPlayer().getColor() == PieceColor.WHITE ? whitePlayer : blackPlayer;
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
        if (neitherKingInGame()) {
            System.out.println(currentPlayer.getOpponent().getLastMove().keySet() + ": "  + currentPlayer.getOpponent().getLastMove().values());
            printBoard();
            throw new IllegalMoveError("Kings can't be captured!");
        }
        // resetting tiles threatened state before every board check
        resetThreatsOnTiles();
        // update all observers
        updateObservers();

        // checking for specific stalemate conditions
        if (currentPlayer.getAlivePieces().size() == 2 && currentPlayer.getOpponent().getAlivePieces().size() == 2) {
            Piece king = currentPlayer.getKing();
            int kingIndex = currentPlayer.getAlivePieces().indexOf(king);
            int pieceIndex = kingIndex == 0 ? 1 : 0;
            Piece piece = currentPlayer.getAlivePieces().get(pieceIndex);

            if (piece instanceof BishopPiece) {
                gameSituation = GameSituation.DRAW;
                isGameOver = true;
                evaluateBoard();
                return;
            }

            // two kings left (allegedly)
        } else if (currentPlayer.getAlivePieces().size() == 1 && currentPlayer.getOpponent().getAlivePieces().size() == 1) {
            gameSituation = GameSituation.DRAW;
            isGameOver = true;
            evaluateBoard();
            return;
        }

        if (currentPlayer.isInCheck()) {
            // reset all legal moves before proceeding to generation of legal moves in check situation
            gameSituation = checkSituations.get(currentPlayer.getColor());
            gameSituation = generateMovesWhenInCheck(currentPlayer);
            double evaluation = checkmateSituations.containsValue(gameSituation) ? 0 : evaluateBoard();
            return;

            // not in check but can't move is a stalemate (/draw)
        } else if (!currentPlayer.isInCheck() && currentPlayer.getMoves().size() == 0) {
            gameSituation = GameSituation.STALEMATE;
            isGameOver = true;
            evaluateBoard();
            return;

        } else {
            if (gameSituation == GameSituation.NORMAL) {
                Map<Piece, List<Tile>> directionsOfThreats = calculatePotentialThreatsForKing(currentPlayer);

                for (Piece opponentPiece : directionsOfThreats.keySet()) {
                    if (opponentPiece != null) {
                        // in each direction of threat, count how many pieces are in between king and threatening piece
                        List<Piece> playerPiecesInTheWay = new ArrayList<>();
                        for (Tile tile : directionsOfThreats.get(opponentPiece)) {
                            if (!tile.isEmpty()) {
                                if (tile.getPiece().getPieceColor() == currentPlayer.getColor()) {
                                    Piece playerPiece = tile.getPiece();
                                    playerPiecesInTheWay.add(playerPiece);
                                } else {
                                    currentPlayer.getKing().getMoves().removeIf(move -> move.getTargetTile().equals(tile));
                                    currentPlayer.getLegalMoves().removeIf(t -> t.equals(tile));
                                    currentPlayer.getKing().getPossibleMoves().removeIf(t -> t.equals(tile));
                                    currentPlayer.getMoves().removeIf(move -> move.getMovingPiece().equals(currentPlayer.getKing())
                                    && move.getTargetTile().equals(tile));
                                }
                            }
                        }
                        // if there's only one player's piece in the way between king and opponent threatening piece, limit it's moves, else not
                        if (playerPiecesInTheWay.size() == 1) {
                            Piece playerPiece = playerPiecesInTheWay.get(0);

                            // removes all player's moves involving playerPiece
                            currentPlayer.getMoves().removeIf(move -> move.getMovingPiece().equals(playerPiece));
                            currentPlayer.getLegalMoves().removeIf(tile -> playerPiece.getPossibleMoves().contains(tile));
                            if (directionsOfThreats.get(opponentPiece).size() > 1) {
                                playerPiece.getPossibleMoves().removeIf(t -> !directionsOfThreats.get(opponentPiece).contains(t));
                                playerPiece.getMoves().removeIf(move -> !directionsOfThreats.get(opponentPiece).contains(
                                        move.getTargetTile()
                                ));
                            } else {
                                playerPiece.getPossibleMoves().removeIf(t -> !t.equals(opponentPiece.getCurrentTile()));
                                playerPiece.getMoves().removeIf(move -> !move.getTargetTile().equals(opponentPiece.getCurrentTile()));
                            }
                            // add all playerPiece's moves after filtering them according to current boar situation
                            currentPlayer.getMoves().addAll(playerPiece.getMoves());
                        }
                    }
                }

                // if game situation is normal but still player has no legal moves, it's a stalemate
                if (currentPlayer.getMoves().size() == 0) {
                    gameSituation = GameSituation.STALEMATE;
                    evaluateBoard();
                    return;
                }
            }
        }
        gameSituation = GameSituation.NORMAL;
        evaluateBoard();
    }

    public Map<Piece, List<Tile>> calculatePotentialThreatsForKing(Player player) {
        // setting potential threatening piece as key of threatened row/column/diagonal
        Map<Piece, List<Tile>> allDirections = new HashMap<>();

        int[][] diagonalDirections = {
                {1, 1},
                {1, -1},
                {-1, -1},
                {-1, 1}
        };

        // horizontal and vertical general directions
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

            // looping through the actual directions
            for (int i = 1; i < board.length; i++) {
                if (kingTileRow + row*i > board.length - 1 || kingTileRow + row*i < 0 ||
                kingTileCol + col*i > board.length - 1 || kingTileCol + col*i < 0) continue;

                Tile currentTile =  board[kingTileRow + row*i][kingTileCol + col*i];
                currentTiles.add(currentTile);

                if (!currentTile.isEmpty()) {
                    if (currentTile.getPiece().getPieceColor() != player.getColor()) {
                        Piece opponentPiece = currentTile.getPiece();
                        if (opponentPiece instanceof PawnPiece) {
                            // add logic for en passant situations
                            for (int c : new int[] {1, -1}) {
                                if (opponentPiece.getCol() + c < 0 || opponentPiece.getCol() + c >= board.length) continue;
                                if (!board[opponentPiece.getRow()][opponentPiece.getCol() + c].isEmpty() &&
                                        board[opponentPiece.getRow()][opponentPiece.getCol() + c].getPiece().getPieceColor() == player.getColor() &&
                                        board[opponentPiece.getRow()][opponentPiece.getCol() + c].getPiece() instanceof PawnPiece pawn) {
                                    if (pawn.canEnPassant(-c)) {
                                        pawn.getMoves().removeIf(move -> move.getTargetTile().equals(pawn.getEnPassantTile()));
                                        pawn.getPossibleMoves().removeIf(tile -> tile.equals(pawn.getEnPassantTile()));
                                        player.getMoves().removeIf(move -> move.getTargetTile().equals(pawn.getEnPassantTile()));
                                        player.getLegalMoves().removeIf(tile -> tile.equals(pawn.getEnPassantTile()));
                                    }
                                }
                            }
                        }
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
                    if (currentTile.getPiece().getPieceColor() != player.getColor()) {
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

    private void generateMovesWhenInCheckInParallel(Player player) {
        List<Move> actualLegalMoves = new ArrayList<>();
        Board copy = new Board(this);

    }

    public GameSituation generateMovesWhenInCheck(Player player) {
        List<Move> actualLegalMoves = new ArrayList<>();

        for (Move move : new ArrayList<>(player.getMoves())) {
            // setting checkBoard argument as false to prevent a StackOverflow error
            boolean successfulMove = move.makeMove(false, true);
            // if the move wasn't successful, try the next move
            if (!successfulMove) continue;
            // if it did, make all pieces generate moves according to current board state
            else updateObservers();

            // if the move prevented the check, it is legal
            if (!player.isInCheck()) actualLegalMoves.add(move);

            // setting checkBoard argument as false to prevent a StackOverflow error
            // unmake the move and make all pieces generate moves
            move.unmakeMove(false);
            updateObservers();
        }

        // if no legal move exists in this current board's state, it is checkmate
        if (actualLegalMoves.size() == 0) {
            return checkmateSituations.get(player.getColor());
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

    public int distanceBetweenPieces(Piece first, Piece second) {
        int distance = 0;

        int firstCol = first.getCurrentTile().getCol();
        int firstRow = first.getCurrentTile().getRow();

        int secondCol = second.getCurrentTile().getCol();
        int secondRow = second.getCurrentTile().getRow();

        // same col
        if (Math.abs(firstCol - secondCol) == 0) {
            distance = Math.abs(firstRow - secondRow);
            // same row
        } else if (Math.abs(firstRow - secondRow) == 0) {
            distance = Math.abs(firstCol - secondCol);
            // different col and row
        } else {
            // can be both
            // distance = Math.abs(firstCol - secondCol);
            distance = Math.abs(firstRow - secondRow);
        }
        return distance;
    }

    public void removePieceFromBoard(Piece piece) {
        piece.setIsAlive(false);
        piece.setCurrentTile(null);
    }

    public void printBoard() {
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

    public void printBoard(Tile[][] board) {
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

    public void setBoard(Tile[][] board) {
        this.board = board;
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

    public Tile getTileByNumberTo63(int num) {
        if (num > 63 || num < 0) return null;
        int[] minInRow = {0, 8, 16, 24, 32, 40, 48, 56};
        int[] maxInRow = {7, 15, 23, 31, 39, 47, 55, 63};

        Tile tile = null;

        for (int i = 0; i < 8; i++) {
            if (num >= minInRow[i] && num <= maxInRow[i]) {
                int row = i;
                int col = num - minInRow[i];

                tile = board[row][col];
            }
        }

        return tile;
    }

    public double evaluateBoard() {
        return blackPlayer.evaluatePlayerScore() + whitePlayer.evaluatePlayerScore();
//        return perspective * (currentPlayer.getPlayerScore() - currentPlayer.getOpponentPlayer().getPlayerScore());
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        currentPlayer.setIsCurrentPlayer(true);
        currentPlayer.getOpponent().setIsCurrentPlayer(false);
        this.currentPlayer = currentPlayer;
    }

    public Stack<Move> getMatchPlays() {
        return matchPlays;
    }

    public boolean isGameOver() {
        if (!whitePlayer.getKing().isAlive() || !blackPlayer.getKing().isAlive()) return true;
        return isGameOver;
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

    public Stack<Tile[][]> getBoardStates() {
        return boardStates;
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
        for (Piece piece : whitePlayer.getAlivePieces()) piece.setIsInDanger(false);
        for (Piece piece : blackPlayer.getAlivePieces()) piece.setIsInDanger(false);

        Tile kingTile = currentPlayer.getKing().getCurrentTile();

//        currentPlayer.getKing().setCurrentTile(null);

        currentPlayer.getOpponent().update();
//        currentPlayer.getKing().setCurrentTile(kingTile);
        currentPlayer.update();

        states.add(this);
        boardStates.add(board);
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

    public boolean neitherKingInGame() {
        return !whitePlayer.getKing().isAlive() || !blackPlayer.getKing().isAlive();
    }
}
