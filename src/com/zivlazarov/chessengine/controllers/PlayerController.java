package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.utils.Board;
import com.zivlazarov.chessengine.model.utils.Piece;
import com.zivlazarov.chessengine.model.utils.Tile;
import com.zivlazarov.chessengine.ui.Player;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerController {

    private Player player;
    private Player opponentPlayer;

    private Board board;

    // insert model and ui pointer (player and board)
//
//    public PlayerController(Player player) {
//        this.player = player;
//    }

    public PlayerController(Player player, Player opponentPlayer) {
        this.player = player;
        this.opponentPlayer = opponentPlayer;
    }

    public PlayerController(Player player, Player opponentPlayer, Board board) {
        this.player = player;
        this.opponentPlayer = opponentPlayer;
        this.board = board;
    }

    public void movePiece(Piece piece, Tile targetTile) {
        player.movePiece(piece, targetTile);
    }

    public void updateStatusOfPiece(Piece piece, boolean isAlive) {
        if (isAlive) player.updatePieceAsAlive(piece);
        else player.updatePieceAsDead(piece);
    }

    public void addAlivePieces(Piece[] pieces, Predicate<? super Piece> predicate) {
        player.getAlivePieces().addAll(Arrays.stream(pieces)
        .filter(predicate)
        .collect(Collectors.toList()));
    }

    public void addAlivePieces(Piece[] pieces) {
        player.getAlivePieces().addAll(Arrays.stream(pieces)
        .filter(piece -> piece.getPieceColor() == player.getPlayerColor())
        .collect(Collectors.toList()));
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setPlayerName(String name) {
        player.setName(name);
    }

    public void setOpponentPlayer(Player opponentPlayer) {
        this.opponentPlayer = opponentPlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }
}
