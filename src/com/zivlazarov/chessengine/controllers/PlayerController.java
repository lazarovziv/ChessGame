package com.zivlazarov.chessengine.controllers;

import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.RookPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.board.Tile;

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

    public void kingSideCastle(KingPiece kingPiece, RookPiece rookPiece) {
        player.kingSideCastle(kingPiece, rookPiece);
    }

    public void queenSideCastle(KingPiece kingPiece, RookPiece rookPiece) {
        player.queenSideCastle(kingPiece, rookPiece);
    }

    public void promotePawn(PawnPiece pawnPiece, String pieceName) {
        player.promotePawn(pawnPiece, pieceName);
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

    public void setNumOfKings(int num) {
        player.setNumOfKings(num);
    }

    public void setNumOfQueens(int num) {
        player.setNumOfQueens(num);
    }

    public void setNumOfBishops(int num) {
        player.setNumOfBishops(num);
    }

    public void setNumOfKnights(int num) {
        player.setNumOfKnights(num);
    }

    public void setNumOfRooks(int num) {
        player.setNumOfRooks(num);
    }

    public void setNumOfPawns(int num) {
        player.setNumOfPawns(num);
    }

    public int getNumOfKings() {
        return player.getNumOfKings();
    }

    public int getNumOfQueens() {
        return player.getNumOfQueens();
    }

    public int getNumOfBishops() {
        return player.getNumOfBishops();
    }

    public int getNumOfKnights() {
        return player.getNumOfKnights();
    }

    public int getNumOfRooks() {
        return player.getNumOfRooks();
    }

    public int getNumOfPawns() {
        return player.getNumOfPawns();
    }

    public boolean hasPlayerPlayedThisTurn() {
        return player.hasPlayedThisTurn();
    }

    public void setHasPlayerPlayedThisTurn(boolean played) {
        player.setHasPlayedThisTurn(played);
    }
}
