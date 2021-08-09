package com.zivlazarov.chessengine.client.model.move;

import com.zivlazarov.chessengine.client.model.board.Board;
import com.zivlazarov.chessengine.client.model.board.Tile;
import com.zivlazarov.chessengine.client.model.pieces.*;
import com.zivlazarov.chessengine.client.model.player.Player;
import com.zivlazarov.chessengine.client.model.utils.Pair;

import java.io.Serial;
import java.io.Serializable;

public class Move implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private int id;

    private Player player;

    private Piece movingPiece;

    private Tile targetTile;

    private transient Board board;

    private String label;

    public Move() {}

    private Move(Board board, Player player, Piece movingPiece, Tile targetTile) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.targetTile = targetTile;
    }

    public boolean makeMove(boolean checkBoard) {
        if (player.getMoves().size() == 0) return false;
//        if (player.getMoves().stream().noneMatch(move -> move.equals(this))) return false;
//        if (!movingPiece.getPossibleMoves().contains(targetTile)) return false;
//        if (!player.getLegalMoves().contains(targetTile)) return false;
//        if (!player.getMoves().contains(this)) return false;

        player.getLastMove().clear();

        Tile currentTile = movingPiece.getCurrentTile();
        if (currentTile != null) player.clearTileFromPiece(currentTile);

        boolean isSpecialMove = false;

        if (movingPiece instanceof PawnPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
            isSpecialMove = player.handleEnPassantMove(movingPiece, targetTile);
            if (isSpecialMove) label = "En Passant";
            movingPiece = player.handlePawnPromotion(movingPiece, targetTile);
            if (movingPiece instanceof QueenPiece) label = "Pawn Promotion";
        } else if (movingPiece instanceof KingPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
            isSpecialMove = player.handleKingSideCastling(movingPiece, targetTile);
            if (isSpecialMove) label = "King Side Castle";
            if (!isSpecialMove) {
                isSpecialMove = player.handleQueenSideCastling(movingPiece, targetTile);
                if (isSpecialMove) label = "Queen Side Castle";
            }
        } else if (movingPiece instanceof RookPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
        }

        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != player.getPlayerColor()) {
            movingPiece.getCapturedPieces().push(targetTile.getPiece());
            player.getOpponentPlayer().addPieceToDead(targetTile.getPiece());
            label = "Capture";
        }

        movingPiece.setLastTile(currentTile);
        movingPiece.setCurrentTile(targetTile);

        // adding the move to piece's and game log
        movingPiece.getHistoryMoves().push(targetTile);
        board.getGameHistoryMoves().push(new Pair<>(movingPiece, targetTile));
        board.pushMoveToMatchPlays(this);

        player.getLastMove().put(movingPiece, new Pair<>(currentTile, targetTile));

        player.resetPlayerScore();
        player.evaluatePlayerScore();

        player.getOpponentPlayer().resetPlayerScore();
        player.getOpponentPlayer().evaluatePlayerScore();

        board.setCurrentPlayer(player.getOpponentPlayer());

        if (checkBoard)
            board.checkBoard();

        return true;
    }

    public void unmakeMove(boolean checkBoard) {
        Tile previousTile = movingPiece.getLastTile();
        Tile currentTile = movingPiece.getCurrentTile();

        if (currentTile == null) return;

        Piece capturedPiece;

        // getting last captured piece
        if (movingPiece.getCapturedPieces().size() > 0) {
            capturedPiece = movingPiece.getLastCapturedPiece();

            // if CAPTURED piece's last tile is the last move's previous tile, return the CAPTURED piece to the game
            // and place CAPTURED piece in that tile while clearing the CAPTURING piece from there
            if (currentTile.equals(capturedPiece.getLastTile())) {
                // order of things:
                // 1. set capturing piece back to previous tile
                // 2. remove captured piece from deadPieces
                // 3. set captured piece in it's previous tile

                // adding the CAPTURED piece to the game
                player.getOpponentPlayer().addPieceToAlive(capturedPiece);
                // removing the CAPTURED piece from capturing piece's capturedPieces list
                movingPiece.getCapturedPieces().remove(movingPiece.getCapturedPieces().size() - 1);
                // clearing CAPTURING piece from it's tile
                player.clearTileFromPiece(currentTile);
                // setting CAPTURED piece's tile
                capturedPiece.setCurrentTile(currentTile);
                // setting CAPTURING piece to it's previous tile
                movingPiece.setCurrentTile(previousTile);
            }
            // no capturing involved in last move
        } else {
            // if last tile is not empty then clear it and set piece to it's previous tile
//            if (currentTile != null) {}
            if (!currentTile.isEmpty()) player.clearTileFromPiece(currentTile);
            movingPiece.setCurrentTile(previousTile);
        }

        // if it was their first move, revert their hasMoved field to false
        if (movingPiece.getHistoryMoves().size() <= 1) {
            if (movingPiece instanceof PawnPiece) movingPiece.setHasMoved(false);
            else if (movingPiece instanceof RookPiece) movingPiece.setHasMoved(false);
            else if (movingPiece instanceof KingPiece) movingPiece.setHasMoved(false);
        }

        // deleting last move made from logs
        if (movingPiece.getHistoryMoves().size() > 0)
            movingPiece.getHistoryMoves().removeElementAt(movingPiece.getHistoryMoves().size() - 1);
        if (board.getGameHistoryMoves().size() > 0)
            board.getGameHistoryMoves().removeElementAt(board.getGameHistoryMoves().size() - 1);
        player.getLastMove().remove(movingPiece);
        if (board.getMatchPlays().size() > 0)
            board.getMatchPlays().removeElementAt(board.getMatchPlays().size() - 1);

        board.setCurrentPlayer(board.getCurrentPlayer());

        if (checkBoard)
            board.checkBoard();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getLabel() {
        return label;
    }

    public String toString() {
      return movingPiece.getName() + ": " + movingPiece.getLastTile() + " -> " + targetTile;
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
