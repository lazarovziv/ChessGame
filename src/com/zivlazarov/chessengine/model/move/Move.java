package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

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

        board.setCurrentPlayer(player);

        return true;
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
