package com.zivlazarov.chessengine.model.pieces;

import com.zivlazarov.chessengine.model.board.Board;
import com.zivlazarov.chessengine.model.board.PieceColor;
import com.zivlazarov.chessengine.model.board.Tile;
import com.zivlazarov.chessengine.model.move.Move;
import com.zivlazarov.chessengine.model.player.Player;
import com.zivlazarov.chessengine.model.utils.Pair;

import javax.persistence.Entity;

@Entity
public class PawnPiece extends Piece implements Cloneable {

    private boolean executedEnPassant = false;
    private boolean movedLong = false;

    private Tile enPassantTile;

    private static final int[] eatingDirections = new int[]{-1, 1};

    public PawnPiece(Player player, Board board, Tile initTile, int pieceCounter) {
        super();

        this.player = player;
        this.board = board;
        this.pieceColor = player.getColor();
        this.currentTile = initTile;
        this.lastTile = currentTile;
        this.pieceCounter = pieceCounter;

        this.value = 10;

        if (this.pieceColor == PieceColor.BLACK) {
            this.name = "bP";
            this.imageName = "blackPawn.png";
        }
        if (this.pieceColor == PieceColor.WHITE) {
            this.name = "wP";
            this.imageName = "whitePawn.png";
        }

        this.player.addPieceToAlive(this);
        this.currentTile.setPiece(this);
        this.pieceType = PieceType.PAWN;

        strongTiles = new double[][] {
                {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0},
                {5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0},
                {1.0, 1.0, 2.0, 3.0, 3.0, 2.0, 1.0, 1.0},
                {0.5, 0.5, 1.0, 2.5, 2.5, 1.0, 0.5, 0.5},
                {0.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, 0.0},
                {0.5, -0.5, -1.0, 0.0, 0.0, -1.0, -0.5, 0.5},
                {0.5, 1.0, 1.0, -2.0, -2.0, 1.0, 1.0, 0.5},
                {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}
        };

        if (pieceColor == PieceColor.BLACK) {
            strongTiles = revertStrongTiles(strongTiles);
        }

        pieceIndex = player.getAlivePieces().size() - 1;
    }

    @Override
    public void generateMoves() {
        if (!isAlive) return;

        int x = currentTile.getRow();
        int y = currentTile.getCol();

        boolean canMoveFurther;

        if (pieceColor == PieceColor.WHITE) {
            canMoveFurther = !hasMoved && x == 1;
        } else {
            canMoveFurther = !hasMoved && x == 6;
        }

        int direction = player.getPlayerDirection();
        int longDirection = direction * 2;

        if (x + player.getPlayerDirection() > board.getBoard().length - 1 || x + player.getPlayerDirection() < 0) return;

        if (board.getBoard()[x+direction][y].isEmpty()) {
            possibleMoves.add(board.getBoard()[x + direction][y]);
            Move move = new Move.Builder()
                    .board(board)
                    .player(player)
                    .movingPiece(this)
                    .targetTile(board.getBoard()[x+direction][y])
                    .build();
            moves.add(move);
            if (canMoveFurther) {
                if (x + longDirection < 0 || x + longDirection > board.getBoard().length - 1) return;
                if (board.getBoard()[x+longDirection][y].isEmpty()) {
                    possibleMoves.add(board.getBoard()[x+longDirection][y]);
                    Move move1 = new Move.Builder()
                            .board(board)
                            .player(player)
                            .movingPiece(this)
                            .targetTile(board.getBoard()[x+longDirection][y])
                            .build();
                    moves.add(move1);
                }
            }
        }
        for (int d : eatingDirections) {
            if (y + d > board.getBoard().length - 1 || y + d < 0) continue;
            if (!board.getBoard()[x+direction][y+d].isEmpty() &&
                    board.getBoard()[x+direction][y+d].getPiece().getPieceColor() != pieceColor) {

                possibleMoves.add(board.getBoard()[x+direction][y+d]);
                piecesUnderThreat.add(board.getBoard()[x+direction][y+d].getPiece());
                board.getBoard()[x+direction][y+d].getPiece().setIsInDanger(true);

                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(board.getBoard()[x+direction][y+d])
                        .build();
                moves.add(move);
            }
            // setting potential capturing tiles as threats
            board.getBoard()[x+direction][y+d].setThreatenedByColor(pieceColor, true);
            // en passant
            if (canEnPassant(d)) {
                possibleMoves.add(enPassantTile);
                Move move = new Move.Builder()
                        .board(board)
                        .player(player)
                        .movingPiece(this)
                        .targetTile(enPassantTile)
                        .build();
                moves.add(move);
                // setting the adjacent pawn piece as under threat
                // only move in chess where piece can be eaten without moving to it's tile
                piecesUnderThreat.add(board.getBoard()[x][y+d].getPiece());
                enPassantTile.setThreatenedByColor(pieceColor, true);
            }
        }
        player.getLegalMoves().addAll(possibleMoves);
        player.getMoves().addAll(moves);
    }

    public boolean canEnPassant(int eatingDirection) {
        int x = currentTile.getRow();
        int y = currentTile.getCol();

        // checking borders of board
        if (y + eatingDirection < 0 || y + eatingDirection > board.getBoard().length - 1
        || x - 2 * player.getOpponent().getPlayerDirection() < 0) return false;

        // checking if piece next to pawn is of type pawn and is opponent's piece
        if (board.getBoard()[x][y + eatingDirection].getPiece() instanceof PawnPiece pawn &&
               pawn.getPieceColor() != pieceColor && !pawn.hasExecutedEnPassant() &&
                board.getGameHistoryMoves().size() > 1 && pawn.hasMovedLong()) {

            // checking to see if opponent's last move is pawn's move 2 tiles forward
            if (player.getOpponent().getLastMove().get(pawn) != null) {
                if (pawn.hasMovedLong()) {
                    enPassantTile = board.getBoard()[x+ player.getPlayerDirection()][y+eatingDirection];
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasExecutedEnPassant() {
        return executedEnPassant;
    }

    public void setExecutedEnPassant(boolean executedEnPassant) {
        this.executedEnPassant = executedEnPassant;
    }

    public Tile getEnPassantTile() {
        return enPassantTile;
    }

    public void setEnPassantTile(Tile enPassantTile) {
        this.enPassantTile = enPassantTile;
    }

    public boolean hasMovedLong() {
        return movedLong;
    }

    public void setMovedLong(boolean movedLong) {
        this.movedLong = movedLong;
    }

    public Piece clone(Board newBoard, Player player) {
        PawnPiece newPawn = new PawnPiece(player, newBoard, newBoard.getBoard()[currentTile.getRow()][currentTile.getCol()], pieceCounter);
        newPawn.setLastTile(newBoard.getBoard()[lastRow][lastCol]);
        if (hasMoved) newPawn.setHasMoved(true);
        else newPawn.setMovedLong(false);

        if (enPassantTile != null) {
            if (!executedEnPassant) {
                newPawn.setExecutedEnPassant(false);
                newPawn.setEnPassantTile(enPassantTile);
            }
        }
        newPawn.setPieceIndex(pieceIndex);
        return newPawn;
    }
}
