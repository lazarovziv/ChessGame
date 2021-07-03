package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.KingPiece;
import com.zivlazarov.chessengine.model.pieces.PawnPiece;
import com.zivlazarov.chessengine.model.pieces.Piece;
import com.zivlazarov.chessengine.model.player.Player;

public class Move {

    private final Board board;

    private final Player player;

    private final Piece movingPiece;
    private final Piece eatenPiece;

    private final Tile currentTile;
    private final Tile targetTile;

    private MoveLabel moveLabel;

    private Move(Board board, Player player, Piece movingPiece, Piece eatenPiece, Tile currentTile, Tile targetTile, MoveLabel moveLabel) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.eatenPiece = eatenPiece;
        this.currentTile = currentTile;
        this.targetTile = targetTile;
        this.moveLabel = moveLabel;
    }

    public void makeMove() {
        if (!movingPiece.canMove()) return;

        boolean isCapturing = !targetTile.isEmpty();
        boolean isEnPassant = movingPiece instanceof PawnPiece && targetTile.equals(((PawnPiece) movingPiece).getEnPassantTile());
        boolean isKingCastling = movingPiece instanceof KingPiece && targetTile.equals(((KingPiece) movingPiece).getKingSideCastleTile());
        boolean isQueenCastling = movingPiece instanceof KingPiece && targetTile.equals(((KingPiece) movingPiece).getQueenSideCastleTile());

        if (isEnPassant) moveLabel = MoveLabel.EN_PASSANT;
        if (isKingCastling) moveLabel = MoveLabel.KING_SIDE_CASTLING;
        if (isQueenCastling) moveLabel = MoveLabel.QUEEN_SIDE_CASTLING;

        if (isCapturing) {
            player.getOpponentPlayer().addPieceToDead(targetTile.getPiece());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Piece getEatenPiece() {
        return eatenPiece;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }

    public MoveLabel getMoveLabel() {
        return moveLabel;
    }

    public static class Builder {

        private Board board;

        private Player player;

        private Piece movingPiece;
        private Piece eatenPiece;

        private Tile currentTile;
        private Tile targetTile;

        private MoveLabel moveLabel;

        public Builder() {}

        public Builder board(Board board) {
            this.board = board;
            return this;
        }

        public Builder player(Player player) {
            this.player = player;
            return this;
        }

        public Builder movingPiece(Piece piece) {
            this.movingPiece = piece;
            return this;
        }

        public Builder eatenPiece(Piece piece) {
            this.eatenPiece = piece;
            return this;
        }

        public Builder currentTile(Tile tile) {
            this.currentTile = tile;
            return this;
        }

        public Builder targetTile(Tile tile) {
            this.targetTile = tile;
            return this;
        }

        public Builder moveLabel(MoveLabel label) {
            this.moveLabel = label;
            return this;
        }

        public Move build() {
            return new Move(board, player, movingPiece, eatenPiece, currentTile, targetTile, moveLabel);
        }
    }
}
