package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.util.ArrayList;

public class Move {

    private final Board board;
    private final Player player;
    private final Piece movingPiece;
    private final Tile targetTile;

    private Move(Board board, Player player, Piece movingPiece, Tile targetTile) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.targetTile = targetTile;
    }

    public boolean makeMove() {
        if (!movingPiece.getPossibleMoves().contains(targetTile)) return false;
        if (!player.getLegalMoves().contains(targetTile)) return false;
//        if (!player.getMoves().contains(this)) return false;

        player.getLastMove().clear();

        Tile currentTile = movingPiece.getCurrentTile();
        if (currentTile != null) player.clearTileFromPiece(currentTile);

        boolean isSpecialMove = false;

        if (movingPiece instanceof PawnPiece) {
            isSpecialMove = player.handleEnPassantMove(movingPiece, targetTile);
            ((PawnPiece) movingPiece).setHasMoved(true);
            player.handlePawnPromotion(movingPiece);
        } else if (movingPiece instanceof KingPiece) {
            isSpecialMove = player.handleKingSideCastling(movingPiece, targetTile);
            if (!isSpecialMove) isSpecialMove = player.handleQueenSideCastling(movingPiece, targetTile);
            ((KingPiece) movingPiece).setHasMoved(true);
        }

        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != player.getPlayerColor()) {
            movingPiece.getPiecesEaten().push(targetTile.getPiece());
            player.getOpponentPlayer().addPieceToDead(targetTile.getPiece());
        }

        movingPiece.setCurrentTile(targetTile);
        movingPiece.setLastTile(targetTile);

        movingPiece.getHistoryMoves().push(targetTile);
        board.getGameHistoryMoves().push(new Pair<>(movingPiece, targetTile));

        player.getLastMove().put(movingPiece, new Pair<>(currentTile, targetTile));

        player.resetPlayerScore();
        player.evaluatePlayerScore();

        player.getOpponentPlayer().resetPlayerScore();
        player.getOpponentPlayer().evaluatePlayerScore();

        board.setCurrentPlayer(player.getOpponentPlayer());

        board.checkBoard(board.getCurrentPlayer());

        return true;
    }

    public void unmakeMove() {
        Piece piece = new ArrayList<>(player.getLastMove().keySet()).get(0);
        Tile previousTile = player.getLastMove().get(piece).getFirst();
        Tile currentTile = player.getLastMove().get(piece).getSecond();

        Piece eatenPiece = null;

        // getting last eaten piece
        if (piece.getPiecesEaten().size() != 0) {
            eatenPiece = piece.getLastPieceEaten();

            // if eaten piece's last tile is the last move's previous tile, return the eaten piece to the game
            // and place eaten piece in that tile while clearing the current piece from there
            if (currentTile.equals(eatenPiece.getLastTile())) {
                player.addPieceToAlive(eatenPiece);
                player.clearTileFromPiece(currentTile);
                eatenPiece.setCurrentTile(currentTile);
            }
        }
        // if last tile is not empty then clear it and set piece to it's previous tile
        if (!currentTile.isEmpty()) player.clearTileFromPiece(currentTile);
        piece.setCurrentTile(previousTile);
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
