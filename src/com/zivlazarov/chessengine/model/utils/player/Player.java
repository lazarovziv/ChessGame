package com.zivlazarov.chessengine.model.utils.player;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.utils.Pair;
import com.zivlazarov.chessengine.model.utils.board.Board;
import com.zivlazarov.chessengine.model.utils.board.PieceColor;
import com.zivlazarov.chessengine.model.utils.board.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Player {

    private Player opponentPlayer;

    private Board board;
    
    private PieceColor playerColor;
    private String name;
    private List<Piece> alivePieces;
    private List<Piece> deadPieces;
    private boolean hasWonGame;

    private int playerDirection;

    private Pair<Tile, Tile> lastMove;

    public Player(Board b, PieceColor pc) {
        board = b;
        playerColor = pc;
        alivePieces = new ArrayList();
        deadPieces = new ArrayList();
        hasWonGame = false;

        // setting player direction, white goes up the board, black goes down (specifically to pawn pieces and for checking pawn promotion)
        if (playerColor == PieceColor.WHITE) {
            playerDirection = 1;
        } else playerDirection = -1;
    }
    
    public Player(Board b, PieceColor pc, String name) {
        board = b;
        playerColor = pc;
        this.name = name;
        alivePieces = new ArrayList();
        deadPieces = new ArrayList();
        hasWonGame = false;
    }

    public void updatePieceAsDead(Piece piece) {
        piece.setIsAlive(false);
        opponentPlayer.addPieceToDead(piece);
    }

    public void updatePieceAsAlive(Piece piece) {
        piece.setIsAlive(true);
        addPieceToAlive(piece);
    }
    
    public void movePiece(Piece piece, Tile targetTile) {
        Tile currentTile = piece.getCurrentTile();
        if (alivePieces.contains(piece)) {
            piece.moveToTile(targetTile);
            lastMove = null;
            lastMove = new Pair<>(currentTile, targetTile);
        }
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
    }
    
    public void addPieceToAlive(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            alivePieces.add(piece);
        }
    }
    
    public void addPieceToDead(Piece piece) {
        if (piece.getPieceColor() == playerColor) {
            deadPieces.add(piece);
        }
    }

    public void addAlivePieces(Piece[] pieces) {
        alivePieces.addAll(Arrays.stream(pieces)
        .filter(piece -> piece.getPieceColor() == playerColor)
        .collect(Collectors.toList()));
    }

    public KingPiece getKing() {
        for (Piece piece : alivePieces) {
            if (piece.getName().contains("K")) return (KingPiece) piece;
        }
        return null;
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

    public void setOpponentPlayer(Player op) {
        opponentPlayer = op;
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
}