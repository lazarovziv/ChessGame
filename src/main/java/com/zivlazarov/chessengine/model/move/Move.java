package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.errors.IllegalMoveError;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.pieces.*;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

import java.io.Serial;
import java.io.Serializable;

public class Move implements Serializable {

    @Serial
    private static final long serialVersionUID = 3L;

    private int id;

    private Player player;

    private Piece movingPiece;

    private Tile sourceTile;

    private Tile targetTile;

    private transient Board board;

    private MoveLabel label;

    public Move() {}

    private Move(Board board, Player player, Piece movingPiece, Tile targetTile) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.sourceTile = movingPiece.getCurrentTile();
        this.targetTile = targetTile;
        label = MoveLabel.REGULAR;
    }

    public boolean makeMove(boolean checkBoard) throws IllegalMoveError {
//        if (player.getMoves().size() == 0) return false; /*throw new IllegalMoveError("No Moves Available!");*/
        if (player.getMoves().stream().noneMatch(move -> move.equals(this))) return false; /*throw new IllegalMoveError("Illegal Move!");*/
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
            if (isSpecialMove) label = MoveLabel.EN_PASSANT;
            Piece opponentPiece = null;
            if (!targetTile.isEmpty()) {
                opponentPiece = targetTile.getPiece();
            }
            movingPiece = player.handlePawnPromotion(movingPiece, targetTile);
            if (movingPiece instanceof QueenPiece) {
                label = MoveLabel.PAWN_PROMOTION;
                // promoted with a capture
                movingPiece.getCapturedPieces().push(opponentPiece);
                player.getOpponentPlayer().addPieceToDead(opponentPiece);

                isSpecialMove = true;
            }
        } else if (movingPiece instanceof KingPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
            isSpecialMove = player.handleKingSideCastling(movingPiece, targetTile);
            if (isSpecialMove) label = MoveLabel.KING_SIDE_CASTLE;
            if (!isSpecialMove) {
                isSpecialMove = player.handleQueenSideCastling(movingPiece, targetTile);
                if (isSpecialMove) label = MoveLabel.QUEEN_SIDE_CASTLE;
            }
        } else if (movingPiece instanceof RookPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
        }

        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != player.getPlayerColor()) {
            movingPiece.getCapturedPieces().push(targetTile.getPiece());
            player.getOpponentPlayer().addPieceToDead(targetTile.getPiece());
            label = MoveLabel.CAPTURE;
        }

        movingPiece.setLastTile(currentTile);
        movingPiece.setCurrentTile(targetTile);

        // adding the move to piece's and game log
        movingPiece.getHistoryMoves().push(targetTile);
        board.getGameHistoryMoves().push(new Pair<>(movingPiece, targetTile));
        board.pushMoveToMatchPlays(this);

        player.getLastMove().put(movingPiece, new Pair<>(currentTile, targetTile));

        player.resetPlayerScore();
        player.getOpponentPlayer().resetPlayerScore();
        player.evaluatePlayerScore();
        player.getOpponentPlayer().evaluatePlayerScore();

        board.setCurrentPlayer(player.getOpponentPlayer());

        if (checkBoard)
            board.checkBoard();

        return true;
    }

    public void unmakeMove(boolean checkBoard) {
        switch (label) {
            case REGULAR -> {
                player.clearTileFromPiece(targetTile);
                movingPiece.setCurrentTile(sourceTile);
            }
            case PAWN_PROMOTION -> {
                player.clearTileFromPiece(targetTile);
                player.addPieceToDead(movingPiece);
                movingPiece = player.getDeadPieces().get(player.getDeadPieces().size() - 2);
                movingPiece.setCurrentTile(sourceTile);
                player.addPieceToAlive(movingPiece);
            }
            case EN_PASSANT -> {
                player.clearTileFromPiece(targetTile);
                Piece opponentPiece = player.getOpponentPlayer().getDeadPieces().get(player.getOpponentPlayer().getDeadPieces().size() - 1);
                movingPiece.getCapturedPieces().remove(opponentPiece);
                player.getOpponentPlayer().addPieceToAlive(opponentPiece);
                opponentPiece.setCurrentTile(board.getBoard()[targetTile.getRow() - player.getPlayerDirection()][targetTile.getCol()]);
                movingPiece.setCurrentTile(sourceTile);
            }
            case CAPTURE -> {
                player.clearTileFromPiece(targetTile);
                Piece opponentPiece = player.getOpponentPlayer().getDeadPieces().get(player.getOpponentPlayer().getDeadPieces().size() - 1);
                movingPiece.getCapturedPieces().remove(opponentPiece);
                player.getOpponentPlayer().addPieceToAlive(opponentPiece);
                movingPiece.setCurrentTile(sourceTile);
                opponentPiece.setCurrentTile(targetTile);
            }
            case KING_SIDE_CASTLE -> {
                player.clearTileFromPiece(targetTile);
                Piece kingSideRook = player.getKingSideRookPiece();
                kingSideRook.setCurrentTile(board.getBoard()[targetTile.getRow()][7]);
                movingPiece.setCurrentTile(sourceTile);
            }
            case QUEEN_SIDE_CASTLE -> {
                player.clearTileFromPiece(targetTile);
                Piece kingSideRook = player.getQueenSideRookPiece();
                kingSideRook.setCurrentTile(board.getBoard()[targetTile.getRow()][0]);
                movingPiece.setCurrentTile(sourceTile);
            }
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

        board.setCurrentPlayer(player);

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

    public MoveLabel getLabel() {
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

/*
* public void unmakeMove(int check) {
        Tile previousTile = sourceTile;
        Tile currentTile = targetTile;

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
                // 2. remove captured piece from deadPieces and add it to alivePieces
                // 3. set captured piece in it's previous tile

                // clearing CAPTURING piece from it's tile
                player.clearTileFromPiece(currentTile);
                // setting CAPTURING piece to it's previous tile
                movingPiece.setCurrentTile(previousTile);
                // removing the CAPTURED piece from capturing piece's capturedPieces list
                movingPiece.getCapturedPieces().remove(movingPiece.getCapturedPieces().size() - 1);
                // adding the CAPTURED piece to the game
                player.getOpponentPlayer().addPieceToAlive(capturedPiece);
                // setting CAPTURED piece's tile
                capturedPiece.setCurrentTile(currentTile);
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

        board.setCurrentPlayer(player);

        if (check == 1)
            board.checkBoard();
    }*/
