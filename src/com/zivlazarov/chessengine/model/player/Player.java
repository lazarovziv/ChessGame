package com.zivlazarov.chessengine.model.player;

import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.utils.MyObservable;
import com.zivlazarov.chessengine.model.utils.MyObserver;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Player implements MyObserver {

    private Player opponentPlayer;

    private final Board board;
    private MyObservable observable;

    private final PieceColor playerColor;
    private String name;
    private final List<Piece> alivePieces;
    private final List<Piece> deadPieces;
    private final Stack<Piece> eatenPieces;
    private boolean hasWonGame;
    private boolean hasPlayedThisTurn;

    private final List<Tile> legalMoves;
    private final List<Piece> piecesCanMove;

    private int numOfPawns;
    private int numOfKnights;
    private int numOfBishops;
    private int numOfRooks;
    private int numOfKings;
    private int numOfQueens;

    private final int playerDirection;

    private Pair<Tile, Tile> lastMove;

    public Player(Board b, PieceColor pc) {
        board = b;
        playerColor = pc;
        alivePieces = new ArrayList<Piece>();
        deadPieces = new ArrayList<Piece>();
        hasWonGame = false;
        hasPlayedThisTurn = false;
        legalMoves = new ArrayList<>();
        piecesCanMove = new ArrayList<>();
        eatenPieces = new Stack<>();

        // setting player direction, white goes up the board, black goes down (specifically to pawn pieces and for checking pawn promotion)
        if (playerColor == PieceColor.WHITE) {
            playerDirection = 1;
        } else playerDirection = -1;

        numOfKings = 1;
        numOfQueens = 1;
        numOfBishops = 2;
        numOfKnights = 2;
        numOfRooks = 2;
        numOfPawns = 8;
    }

    public void refreshPieces() {
        legalMoves.clear();
        piecesCanMove.clear();
        for (Piece piece : alivePieces) {
            piece.refresh();
            legalMoves.addAll(piece.getPossibleMoves());
            if (piece.canMove()) piecesCanMove.add(piece);
        }
    }

    public void updatePieceAsDead(Piece piece) {
        piece.setIsAlive(false);
        opponentPlayer.addPieceToDead(piece);
    }

    public void updatePieceAsAlive(Piece piece) {
        piece.setIsAlive(true);
        addPieceToAlive(piece);
    }

    public boolean movePiece(Piece piece, Tile targetTile) {
//        if (!alivePieces.contains(piece)) return false;
//        if (!legalMoves.contains(targetTile)) return false;

        Tile pieceTile = piece.getCurrentTile();
        clearTileFromPiece(pieceTile);
        // eat move
        if (!targetTile.isEmpty()) {
            eatenPieces.push(targetTile.getPiece());
            opponentPlayer.addPieceToDead(targetTile.getPiece());
        }

        piece.setCurrentTile(targetTile);
        update();

        piece.getHistoryMoves().push(targetTile);

        lastMove = null;
        lastMove = new Pair<>(pieceTile, targetTile);
        hasPlayedThisTurn = true;

        return true;
    }

    public void kingSideCastle(KingPiece kingPiece, RookPiece rookPiece) {
        if (playerColor == PieceColor.BLACK) {
            movePiece(kingPiece, board.getBoard()[kingPiece.getCurrentTile().getRow()][kingPiece.getCurrentTile().getCol() + 2]);
            movePiece(rookPiece, board.getBoard()[rookPiece.getCurrentTile().getRow()][rookPiece.getCurrentTile().getCol() - 2]);
        }

        if (playerColor == PieceColor.WHITE) {
            movePiece(kingPiece, board.getBoard()[kingPiece.getCurrentTile().getRow()][kingPiece.getCurrentTile().getCol() - 2]);
            movePiece(rookPiece, board.getBoard()[rookPiece.getCurrentTile().getRow()][rookPiece.getCurrentTile().getCol() + 2]);
        }
        hasPlayedThisTurn = true;
    }

    public void queenSideCastle(KingPiece kingPiece, RookPiece rookPiece) {
        if (playerColor == PieceColor.BLACK) {
            movePiece(kingPiece, board.getBoard()[kingPiece.getCurrentTile().getRow()][kingPiece.getCurrentTile().getCol() - 2]);
            movePiece(rookPiece, board.getBoard()[rookPiece.getCurrentTile().getRow()][rookPiece.getCurrentTile().getCol() + 3]);
        }

        if (playerColor == PieceColor.WHITE) {
            movePiece(kingPiece, board.getBoard()[kingPiece.getCurrentTile().getRow()][kingPiece.getCurrentTile().getCol() + 2]);
            movePiece(rookPiece, board.getBoard()[rookPiece.getCurrentTile().getRow()][rookPiece.getCurrentTile().getCol() - 3]);
        }
        hasPlayedThisTurn = true;
    }

    public void promotePawn(PawnPiece pawnPiece, String pieceName) {
        // setting it as dead and adding it to deadPieces list
        pawnPiece.setIsAlive(false);
        addPieceToDead(pawnPiece);
        Tile targetTile = pawnPiece.getCurrentTile();
        Piece piece = null;
        // clearing piece from it's tile and creating a new piece based on user's answer
        switch (pieceName) {
            case "Q", "q" -> {
                clearTileFromPiece(pawnPiece.getCurrentTile());
                piece = new QueenPiece(this, board, playerColor, targetTile);
                numOfQueens++;
            }
            case "R", "r" -> {
                clearTileFromPiece(pawnPiece.getCurrentTile());
                numOfRooks++;
                piece = new RookPiece(this, board, playerColor, targetTile,numOfRooks - 1);
            }
            case "B", "b" -> {
                clearTileFromPiece(pawnPiece.getCurrentTile());
                numOfBishops++;
                piece = new BishopPiece(this, board, playerColor, targetTile,numOfBishops - 1);
            }
            case "N", "n" -> {
                clearTileFromPiece(pawnPiece.getCurrentTile());
                numOfKnights++;
                piece = new KnightPiece(this, board, playerColor, targetTile,numOfKnights - 1);
            }
        }
        if (piece != null) {
            addPieceToAlive(piece);
        }
        hasPlayedThisTurn = true;
    }

    public void addPieceToAlive(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            alivePieces.add(piece);
            deadPieces.remove(piece);
        }
    }

    public void addPieceToDead(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
            alivePieces.remove(piece);
            clearTileFromPiece(piece.getCurrentTile());
            piece.setCurrentTile(null);
            piece.setIsAlive(false);
        }
    }

    public void addAlivePieces(Piece[] pieces) {
        alivePieces.addAll(Arrays.stream(pieces)
                .filter(piece -> piece.getPieceColor() == playerColor)
                .collect(Collectors.toList()));
    }

    public void clearTileFromPiece(Tile tile) {
        tile.setPiece(null);
    }

    public KingPiece getKing() {
        for (Piece piece : alivePieces) {
            if (piece instanceof KingPiece) return (KingPiece) piece;
        }
        return null;
    }

    public int getNumOfPawns() {
        return numOfPawns;
    }

    public void setNumOfPawns(int numOfPawns) {
        this.numOfPawns = numOfPawns;
    }

    public int getNumOfKnights() {
        return numOfKnights;
    }

    public void setNumOfKnights(int numOfKnights) {
        this.numOfKnights = numOfKnights;
    }

    public int getNumOfBishops() {
        return numOfBishops;
    }

    public void setNumOfBishops(int numOfBishops) {
        this.numOfBishops = numOfBishops;
    }

    public int getNumOfRooks() {
        return numOfRooks;
    }

    public void setNumOfRooks(int numOfRooks) {
        this.numOfRooks = numOfRooks;
    }

    public int getNumOfKings() {
        return numOfKings;
    }

    public void setNumOfKings(int numOfKings) {
        this.numOfKings = numOfKings;
    }

    public int getNumOfQueens() {
        return numOfQueens;
    }

    public void setNumOfQueens(int numOfQueens) {
        this.numOfQueens = numOfQueens;
    }

    public PieceColor getPlayerColor() {
        return playerColor;
    }

    public List<Piece> getAlivePieces() {
        return alivePieces;
    }

    public List<Piece> getDeadPieces() {
        return deadPieces;
    }

    public String getName() {
        return name;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public void setOpponentPlayer(Player opponent) {
        opponentPlayer = opponent;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Player other) {
        return playerColor == other.getPlayerColor();
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean hasWonGame() {
        return hasWonGame;
    }

    public void setHasWonGame(boolean hasWonGame) {
        this.hasWonGame = hasWonGame;
    }

    public Pair<Tile, Tile> getLastMove() {
        return lastMove;
    }

    public int getPlayerDirection() {
        return playerDirection;
    }

    public boolean hasPlayedThisTurn() {
        return hasPlayedThisTurn;
    }

    public void setHasPlayedThisTurn(boolean played) {
        hasPlayedThisTurn = played;
    }

    public List<Tile> getLegalMoves() {
        return legalMoves;
    }

    public List<Piece> getPiecesCanMove() {
        return piecesCanMove;
    }

    public void updateLegalMoves() {
        for (Piece piece : alivePieces) {
            legalMoves.addAll(piece.getPossibleMoves());
        }
    }

    public boolean isInCheck() {
        return getKing().getIsInDanger();
//        if (playerColor == PieceColor.WHITE) return board.getGameSituation() == GameSituation.WHITE_IN_CHECK;
//        else return board.getGameSituation() == GameSituation.BLACK_IN_CHECK;
    }

    @Override
    public void update() {
        refreshPieces();
    }

    @Override
    public void setObservable(MyObservable observable) {
        this.observable = observable;
    }
}