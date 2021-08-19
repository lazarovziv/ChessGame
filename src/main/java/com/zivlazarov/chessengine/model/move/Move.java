package com.zivlazarov.chessengine.model.move;

import com.zivlazarov.chessengine.errors.IllegalMoveError;
import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
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

    private Move castlingMove;

    public Move() {}

    private Move(Board board, Player player, Piece movingPiece, Tile targetTile) {
        this.board = board;
        this.player = player;
        this.movingPiece = movingPiece;
        this.sourceTile = movingPiece.getCurrentTile();
        this.targetTile = targetTile;
        label = MoveLabel.REGULAR;
    }

    private Move(Player player, Piece movingPiece, int targetRow, int targetCol) {
        this.board = new Board();
        this.player = player;
        this.movingPiece = movingPiece;
        this.sourceTile = movingPiece.getCurrentTile();
        this.targetTile = board.getBoard()[targetRow][targetCol];
    }

    public boolean makeMove(boolean checkBoard, boolean addToHistory) throws IllegalMoveError {
//        if (player.getMoves().size() == 0) return false; /*throw new IllegalMoveError("No Moves Available!");*/
//        if (player.getMoves().stream().noneMatch(move -> move.equals(this))) return false; /*throw new IllegalMoveError("Illegal Move!");*/
//        if (!movingPiece.getPossibleMoves().contains(targetTile)) return false;
//        if (!player.getLegalMoves().contains(targetTile)) return false;
//        if (!player.getMoves().contains(this)) return false;

        player.getLastMove().clear();

        Tile currentTile = movingPiece.getCurrentTile();
        if (currentTile != null) player.clearPieceFromTile(currentTile);

        boolean isSpecialMove = false;

        if (movingPiece instanceof PawnPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }

            // handling en passant
            if (((PawnPiece) movingPiece).getEnPassantTile() != null) {
                if (targetTile.equals(((PawnPiece) movingPiece).getEnPassantTile())) {
                    player.getOpponent().addPieceToDead(
                            board.getBoard()[((PawnPiece) movingPiece).getEnPassantTile().getRow() - player.getPlayerDirection()]
                                    [((PawnPiece) movingPiece).getEnPassantTile().getCol()].getPiece());
                    ((PawnPiece) movingPiece).setExecutedEnPassant(true);
                    isSpecialMove = true;
                    label = MoveLabel.EN_PASSANT;
                }
            }

            Piece opponentPiece = null;
            if (!targetTile.isEmpty()) {
                opponentPiece = targetTile.getPiece();
            }

            // pawn promotion
            if (movingPiece.getPieceColor() == PieceColor.WHITE) {
                if (targetTile.getRow() == 7) {
                    player.addPieceToDead(movingPiece);
                    movingPiece = new QueenPiece(player, board, targetTile);
                }
            } else {
                if (targetTile.getRow() == 0) {
                    player.addPieceToDead(movingPiece);
                    movingPiece = new QueenPiece(player, board, targetTile);
                }
            }

            if (movingPiece.getPieceType() == PieceType.QUEEN) {
                label = MoveLabel.PAWN_PROMOTION;
                // promoted with a capture
                movingPiece.getCapturedPieces().push(opponentPiece);
                player.getOpponent().addPieceToDead(opponentPiece);

                isSpecialMove = true;
            }
            // handling king moves
        } else if (movingPiece instanceof KingPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }

            // king side castle
            if (targetTile.equals(((KingPiece) movingPiece).getKingSideCastleTile()) &&
            player.getKingSideRookPiece() != null && !((KingPiece) movingPiece).hasExecutedKingSideCastle()
                    && player.getKingSideRookPiece().getCurrentTile() != null) {
                RookPiece kingSideRook = player.getKingSideRookPiece();
//                kingSideRook.setLastTile(kingSideRook.getCurrentTile());
//                kingSideRook.setCurrentTile(kingSideRook.getKingSideCastlingTile());
//                kingSideRook.setHasMoved(true);
                castlingMove = new Move(board, player, kingSideRook, kingSideRook.getKingSideCastlingTile());
                ((KingPiece) movingPiece).setExecutedKingSideCastle(true);

                label = MoveLabel.KING_SIDE_CASTLE;
                isSpecialMove = true;
            }

            // if not king side castling, maybe queen side so isSpecialMove has to be false
            if (!isSpecialMove) {
                if (targetTile.equals(((KingPiece) movingPiece).getQueenSideCastleTile()) &&
                player.getQueenSideRookPiece() != null && !((KingPiece) movingPiece).hasExecutedQueenSideCastle()
                        && player.getQueenSideRookPiece().getCurrentTile() != null) {
                    RookPiece queenSideRook = player.getQueenSideRookPiece();
//                    queenSideRook.setLastTile(queenSideRook.getCurrentTile());
//                    queenSideRook.setCurrentTile(queenSideRook.getQueenSideCastlingTile());
//                    queenSideRook.setHasMoved(true);
                    castlingMove = new Move(board, player, queenSideRook, queenSideRook.getQueenSideCastlingTile());
                    ((KingPiece) movingPiece).setExecutedQueenSideCastle(true);

                    label = MoveLabel.QUEEN_SIDE_CASTLE;
                    isSpecialMove = true;
                }
            }

        } else if (movingPiece instanceof RookPiece) {
            if (checkBoard && !movingPiece.hasMoved()) {
                movingPiece.setHasMoved(true);
            }
        }

        if (!isSpecialMove && !targetTile.isEmpty() && targetTile.getPiece().getPieceColor() != player.getColor()) {
            movingPiece.getCapturedPieces().push(targetTile.getPiece());
            player.getOpponent().addPieceToDead(targetTile.getPiece());
            label = MoveLabel.CAPTURE;
        }

        movingPiece.setLastTile(sourceTile);
        movingPiece.setCurrentTile(targetTile);

        // adding the move to piece's and game log
        movingPiece.getHistoryMoves().push(targetTile);
        if (addToHistory) {
            board.getGameHistoryMoves().push(new Pair<>(movingPiece, targetTile));
            board.pushMoveToMatchPlays(this);
            player.incrementTurn();
        }

        player.getLastMove().put(movingPiece, new Pair<>(currentTile, targetTile));

        if (castlingMove != null) castlingMove.makeMove(false, false);

        board.setCurrentPlayer(player.getOpponent());

        board.evaluateBoard();
        player.addToScore(
                player.getPlayerDirection() * movingPiece.getStrongTiles()[targetTile.getRow()][targetTile.getCol()]);

        if (checkBoard) {
            board.checkBoard();
        }

        return true;
    }

    public void unmakeMove(boolean checkBoard) {
        switch (label) {
            case REGULAR -> {
                player.clearPieceFromTile(targetTile);
                movingPiece.setCurrentTile(sourceTile);

                // if it was their first move, revert their hasMoved field to false
                if (movingPiece.getHistoryMoves().size() <= 1) {
                    if (movingPiece instanceof PawnPiece) movingPiece.setHasMoved(false);
                    else if (movingPiece instanceof RookPiece) movingPiece.setHasMoved(false);
                    else if (movingPiece instanceof KingPiece) movingPiece.setHasMoved(false);
                }
            }
            case PAWN_PROMOTION -> {
                player.clearPieceFromTile(targetTile);
                player.addPieceToDead(movingPiece);
                movingPiece = player.getDeadPieces().get(player.getDeadPieces().size() - 2);
                movingPiece.setCurrentTile(sourceTile);
                player.addPieceToAlive(movingPiece);
            }
            case EN_PASSANT -> {
                player.clearPieceFromTile(targetTile);
                if (player.getOpponent().getDeadPieces().size() > 0) {
                    Piece opponentPiece = player.getOpponent().getDeadPieces().get(player.getOpponent().getDeadPieces().size() - 1);
                    movingPiece.getCapturedPieces().remove(opponentPiece);
                    player.getOpponent().addPieceToAlive(opponentPiece);
                    opponentPiece.setCurrentTile(board.getBoard()[targetTile.getRow() - player.getPlayerDirection()][targetTile.getCol()]);
                    movingPiece.setCurrentTile(sourceTile);
                }
            }
            case CAPTURE -> {
                player.clearPieceFromTile(targetTile);
                if (player.getOpponent().getDeadPieces().size() > 0) {
                    movingPiece.setCurrentTile(sourceTile);
                    Piece opponentPiece = player.getOpponent().getDeadPieces().get(player.getOpponent().getDeadPieces().size() - 1);
                    movingPiece.getCapturedPieces().remove(opponentPiece);
                    player.getOpponent().addPieceToAlive(opponentPiece);
                    opponentPiece.setCurrentTile(targetTile);
                }
            }
            case KING_SIDE_CASTLE -> {
                player.clearPieceFromTile(targetTile);
                Piece kingSideRook = player.getKingSideRookPiece();
                player.clearPieceFromTile(kingSideRook.getCurrentTile());
                kingSideRook.setLastTile(kingSideRook.getCurrentTile());
                kingSideRook.setCurrentTile(board.getBoard()[targetTile.getRow()][7]);
                kingSideRook.setHasMoved(false);
                movingPiece.setHasMoved(false);
//                castlingMove.unmakeMove(true);
                movingPiece.setCurrentTile(sourceTile);
            }
            case QUEEN_SIDE_CASTLE -> {
                player.clearPieceFromTile(targetTile);
                Piece queenSideRook = player.getQueenSideRookPiece();
                player.clearPieceFromTile(queenSideRook.getCurrentTile());
                queenSideRook.setLastTile(queenSideRook.getCurrentTile());
                queenSideRook.setCurrentTile(board.getBoard()[targetTile.getRow()][0]);
                queenSideRook.setHasMoved(false);
                movingPiece.setHasMoved(false);
//                castlingMove.unmakeMove(true);
                movingPiece.setCurrentTile(sourceTile);
            }
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

    public Player getPlayer() {
        return player;
    }

    public Piece getMovingPiece() {
        return movingPiece;
    }

    public Tile getSourceTile() {
        return sourceTile;
    }

    public Tile getTargetTile() {
        return targetTile;
    }

    public MoveLabel getLabel() {
        return label;
    }

    public String toString() {
        if (label == MoveLabel.KING_SIDE_CASTLE) {
            return "King Side Castle";
        } else if (label == MoveLabel.QUEEN_SIDE_CASTLE) {
            return "Queen Side Castle";
        } else if (label == MoveLabel.PAWN_PROMOTION) {
            return "Pawn Promotion: " + movingPiece.getName() + ": " + movingPiece.getLastTile() + " -> " + targetTile;
        } else {
            return movingPiece.getName() + ": " + movingPiece.getLastTile() + " -> " + targetTile;
        }
    }

    public boolean equals(Move move) {
        return move.player.getColor() == player.getColor() &&
                move.movingPiece.getCurrentTile().equals(movingPiece.getCurrentTile()) &&
                move.targetTile.equals(targetTile);
    }

    public static class Builder {

        private Board board;

        private Player player;

        private Piece movingPiece;
        private Tile targetTile;

        private int targetRow;
        private int targetCol;

        public Builder() {}

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
            // add a condition if target tile is in piece's possible moves
//            if (movingPiece.getPossibleMoves().contains(targetTile)) {
//                this.targetTile = targetTile;
//                return this;
//            } else throw new IllegalMoveError();
            this.targetTile = targetTile;
            return this;
        }

        public Builder targetRow(int targetRow) {
            this.targetRow = targetRow;
            return this;
        }

        public Builder targetCol(int targetCol) {
            this.targetCol = targetCol;
            return this;
        }

        public Move build() {
            if (board != null)
                return new Move(board, player, movingPiece, targetTile);
            else return new Move(player, movingPiece, targetRow, targetCol);
        }
    }
}
