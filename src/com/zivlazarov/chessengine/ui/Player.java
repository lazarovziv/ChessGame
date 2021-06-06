package com.zivlazarov.chessengine.ui;

import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.PieceColor;
import com.zivlazarov.chessengine.model.utils.Tile;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private Player opponentPlayer;
    private Board board;
    
    private PieceColor playerColor;
    private String name;
    private List<Piece> alivePieces;
    private List<Piece> deadPieces;
    private boolean hasWonGame;
    private boolean startsGame;

    public Player(Board b, PieceColor pc, boolean startGame) {
        board = b;
        playerColor = pc;
        alivePieces = new ArrayList();
        deadPieces = new ArrayList();
        hasWonGame = false;
        this.startsGame = startGame;
    }
    
    public Player(Board b, PieceColor pc, String name, boolean startsGame) {
        board = b;
        playerColor = pc;
        this.name = name;
        alivePieces = new ArrayList();
        deadPieces = new ArrayList();
        hasWonGame = false;
        this.startsGame = startsGame;
    }
    
    public boolean movePiece(Piece piece, Tile targetTile) {
        if (alivePieces.contains(piece)) {
            if (piece.getTilesToMoveTo().contains(targetTile)) {
                piece.moveToTile(targetTile);
                return true;
            }
        }
        return false;
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
}