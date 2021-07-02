package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.MyObservable;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerController {

    private Player player;

    public PlayerController() {}

    public void movePiece(Piece piece, Tile targetTile) {
        player.movePiece(piece, targetTile);
    }

    public static char receivePawnPromotionChoice() {
        char answer;
        System.out.println("Please enter a promotion for your pawn: (q/b/n/r)");
        Scanner scanner = new Scanner(System.in);
        answer = scanner.nextLine().toLowerCase().charAt(0);
        while (!scanner.hasNextLine() && answer != 'q' && answer != 'b' && answer != 'n' && answer != 'r') {
            System.out.println("Please enter a valid answer: (q/b/n/r)");
            answer = scanner.nextLine().toLowerCase().charAt(0);
        }
        return answer;
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

    public void addPieceToAlive(Piece piece) {
        player.addPieceToAlive(piece);
    }

    public void clearTileFromPiece(Tile tile) {
        player.clearTileFromPiece(tile);
    }

    public void addAlivePieces(Piece[] pieces) {
        player.addAlivePieces(pieces);
    }

    public void addAlivePiecesToOpponent(Piece[] pieces) {
        player.getOpponentPlayer().addAlivePieces(pieces);
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

    public void setOpponentPlayerName(String name) { player.getOpponentPlayer().setName(name); }

    public void setOpponentPlayer(Player opponentPlayer) {
        player.setOpponentPlayer(opponentPlayer);
    }

    public Player getOpponentPlayer() {
        return player.getOpponentPlayer();
    }

    public void attachToObservable(MyObservable observable) {
        player.setObservable(observable);
    }
}
