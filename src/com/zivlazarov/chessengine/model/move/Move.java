package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Move {

    private final Board board;
    private final Player player;
    private Piece movingPiece;
    private final Tile targetTile;

    private Move(Board board, Player player, Piece movingPiece, Tile targetTile) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.targetTile = targetTile;
    }

    public boolean makeMove(boolean checkBoard) {
        if (player.getMoves().stream().noneMatch(move -> move.equals(this))) return false;
//        if (!movingPiece.getPossibleMoves().contains(targetTile)) return false;
//        if (!player.getLegalMoves().contains(targetTile)) return false;
//        if (!player.getMoves().contains(this)) return false;

        player.getLastMove().clear();

        Tile currentTile = movingPiece.getCurrentTile();
        if (currentTile != null) player.clearTileFromPiece(currentTile);

        boolean isSpecialMove = false;

        if (movingPiece instanceof PawnPiece) {
            isSpecialMove = player.handleEnPassantMove(movingPiece, targetTile);
            ((PawnPiece) movingPiece).setHasMoved(true);
            movingPiece = player.handlePawnPromotion(movingPiece, targetTile);
        } else if (movingPiece instanceof KingPiece) {
            isSpecialMove = player.handleKingSideCastling(movingPiece, targetTile);
            if (!isSpecialMove) isSpecialMove = player.handleQueenSideCastling(movingPiece, targetTile);
            ((KingPiece) movingPiece).setHasMoved(true);
        }

        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != player.getPlayerColor()) {
            movingPiece.getCapturedPieces().push(targetTile.getPiece());
            player.getOpponentPlayer().addPieceToDead(targetTile.getPiece());
        }

        movingPiece.setLastTile(currentTile);
        movingPiece.setCurrentTile(targetTile);

        movingPiece.getHistoryMoves().push(targetTile);
        board.getGameHistoryMoves().push(new Pair<>(movingPiece, targetTile));

        player.getLastMove().put(movingPiece, new Pair<>(currentTile, targetTile));

        player.resetPlayerScore();
        player.evaluatePlayerScore();

        player.getOpponentPlayer().resetPlayerScore();
        player.getOpponentPlayer().evaluatePlayerScore();

        board.setCurrentPlayer(player.getOpponentPlayer());

        if (checkBoard)
            board.checkBoard(board.getCurrentPlayer());

        return true;
    }

    public void unmakeMove(boolean checkBoard) {
        Tile previousTile = movingPiece.getLastTile();
        Tile currentTile = movingPiece.getCurrentTile();

        Piece capturedPiece;

        // getting last captured piece
        if (movingPiece.getCapturedPieces().size() != 0) {
            capturedPiece = movingPiece.getLastPieceEaten();

            // if captured piece's last tile is the last move's previous tile, return the captured piece to the game
            // and place captured piece in that tile while clearing the current piece from there
            if (currentTile.equals(capturedPiece.getLastTile())) {
                // adding the captured piece to the game
                player.getOpponentPlayer().addPieceToAlive(capturedPiece);
                // removing the captured piece from capturing piece's capturedPieces list
                movingPiece.getCapturedPieces().remove(movingPiece.getCapturedPieces().size() - 1);
                // clearing capturing piece from it's tile
                player.clearTileFromPiece(currentTile);
                // setting captured piece's tile
                capturedPiece.setCurrentTile(currentTile);
                // setting capturing piece to it's previous tile
                movingPiece.setCurrentTile(previousTile);
            }
        } else {
            // if last tile is not empty then clear it and set piece to it's previous tile
            if (!currentTile.isEmpty()) player.clearTileFromPiece(currentTile);
            movingPiece.setCurrentTile(previousTile);
            // TODO: set hasMoved field for piece on first move as false after unmaking move
        }

        board.setCurrentPlayer(board.getCurrentPlayer());

        if (checkBoard)
            board.checkBoard(board.getCurrentPlayer());
    }

    public Player getPlayer() {
        return player;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Tile getTargetTile() {
        return targetTile;
    }
    
    public String toString() {
      return movingPiece.getName() + ": " + movingPiece.getCurrentTile() + " -> " + targetTile;
    }

    public boolean equals(Move move) {
        return move.player.getPlayerColor() == player.getPlayerColor() &&
                move.movingPiece.getCurrentTile().equals(movingPiece.getCurrentTile()) &&
                move.targetTile.equals(targetTile);
    }

    public static class Builder {

        private Board board;

        private Player player;

        private Piece movingPiece;
        private Tile targetTile;

        public Builder() {
        }

        public Builder board(Board board) {
            this.board = board;
            return this;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder movingPiece(Piece movingPiece) {
            this.movingPiece = movingPiece;
            return this;
        }

        public Builder targetTile(Tile targetTile) {
            this.targetTile = targetTile;
            return this;
        }

        public Move build() {
            return new Move(board, player, movingPiece, targetTile);
        }
    }
}
