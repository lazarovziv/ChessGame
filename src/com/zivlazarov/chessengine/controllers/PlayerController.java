package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.ui.Player;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerController {

    private Player player;

    private Player whitePlayer;
    private Player blackPlayer;

    public PlayerController(Player player) {
        this.player = player;
    }

//    public PlayerController(Player white, Player black) {
//        whitePlayer = white;
//        blackPlayer = black;
//    }
//
//    public boolean movePiece(Piece piece, Tile targetTile) {
//        if (piece.getPieceColor() == player.getPlayerColor()) {
//            player.movePiece(piece, targetTile);
//            return true;
//        } else {
//            throw new PieceError("Can't move " + player.getOpponentPlayer() + "'s pieces!");
//        }
//    }

//    public void addAlivePieces(Piece[] pieces) {
//        player.getAlivePieces().addAll(Arrays.stream(pieces)
//        .filter(piece -> piece.getPieceColor() == player.getPlayerColor())
//        .collect(Collectors.toList()));
//    }

    public void addAlivePieces(Piece[] pieces, Predicate<? super Piece> predicate) {
        player.getAlivePieces().addAll(Arrays.stream(pieces)
        .filter(predicate)
        .collect(Collectors.toList()));
    }

//    public void addDeadPiece(Piece piece) {
//        player.getDeadPieces().add(piece);
//    }

//    public boolean movePiece(Piece piece, Tile targetTile) {
//        if (piece.getPieceColor() == PieceColor.WHITE) {
//            whitePlayer.movePiece(piece, targetTile);
//        } else {
//            blackPlayer.movePiece(piece, targetTile);
//        }
//        return true;
//    }


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerName(String name) {
        player.setName(name);
    }

//    public void setPlayerName(Player player, String name) {
//        if (player.getPlayerColor() == PieceColor.WHITE) whitePlayer.setName(name);
//        else blackPlayer.setName(name);
//    }
//
//    public Player getWhitePlayer() {
//        return whitePlayer;
//    }
//
//    public Player getBlackPlayer() {
//        return blackPlayer;
//    }
}
