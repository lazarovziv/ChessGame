package com.zivlazarov.chessengine.pieces;

import com.zivlazarov.chessengine.Board;
import com.zivlazarov.chessengine.Piece;
import com.zivlazarov.chessengine.PieceColor;
import com.zivlazarov.chessengine.Tile;

import java.util.ArrayList;

public class PawnPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private char name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private boolean hasMoved = false;

    public PawnPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

        name = 'P';
        pieceColor = pc;
        tilesToMoveTo = new ArrayList<Tile>();

        currentTile = initTile;
        board.getAlivePieces().add(this);
        currentTile.setPiece(this);
        generateTilesToMoveTo();
    }

    @Override
    public void generateTilesToMoveTo() {
        int x = currentTile.getX();
        int y = currentTile.getY();

        boolean canMoveFurther = !hasMoved;

        // if it's white, it's only way forward is "down the matrix" which is using a lower x value
        if (pieceColor == PieceColor.WHITE) {
            if (board.getBoard()[x-1][y].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[x-1][y]);
            } else if (board.getBoard()[x-1][y].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[x-1][y]);
            }
            // checking canMoveFurther for another step
            if (canMoveFurther) {
                if (board.getBoard()[x-2][y].isEmpty()) {
                    tilesToMoveTo.add(board.getBoard()[x-2][y]);
                } else if (board.getBoard()[x-2][y].getPiece().getPieceColor() != pieceColor) {
                    tilesToMoveTo.add(board.getBoard()[x-2][y]);
                }
            }
        }
        // if black, forward means "going up the matrix" which is using a higher x value
        if (pieceColor == PieceColor.BLACK) {
            if (board.getBoard()[x+1][y].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[x+1][y]);
            } else if (board.getBoard()[x+1][y].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[x+1][y]);
            }
            if (canMoveFurther) {
                if (board.getBoard()[x+2][y].isEmpty()) {
                    tilesToMoveTo.add(board.getBoard()[x+2][y]);
                } else if (board.getBoard()[x+2][y].getPiece().getPieceColor() != pieceColor) {
                    tilesToMoveTo.add(board.getBoard()[x+2][y]);
                }
            }
        }
    }

    @Override
    public char getName() {
        return name;
    }

    @Override
    public boolean getIsAlive() {
        return isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return false;
    }

    @Override
    public ArrayList<Tile> getTilesToMoveTo() {
        return tilesToMoveTo;
    }

    @Override
    public PieceColor getPieceColor() {
        return pieceColor;
    }

    @Override
    public void setName(char name) {
        this.name = name;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public void setIsInDanger(boolean isInDanger) {
        this.isInDanger = isInDanger;
    }

    @Override
    public void setPieceColor(PieceColor pieceColor) {
        this.pieceColor = pieceColor;
    }

    @Override
    public boolean isThreatenedAtTile(Tile tile) {
        if (pieceColor == PieceColor.WHITE) {
            if (tile.isThreatenedByBlack()) return true;
            else return false;
        }
        if (pieceColor == PieceColor.BLACK) {
            if (tile.isThreatenedByWhite()) return true;
            else return false;
        }
        return false;
    }

    @Override
    public void moveToTile(Tile tile) {
        if (tilesToMoveTo.contains(tile)) {
            // clear current tile
            currentTile.setPiece(null);
            // change to selected tile
            currentTile = tile;
            // set the piece at selected tile
            currentTile.setPiece(this);
            tilesToMoveTo.clear();
            hasMoved = true;
            generateTilesToMoveTo();
        }
    }
}
