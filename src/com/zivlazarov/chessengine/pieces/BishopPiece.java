package com.zivlazarov.chessengine.pieces;

import com.zivlazarov.chessengine.Board;
import com.zivlazarov.chessengine.Piece;
import com.zivlazarov.chessengine.PieceColor;
import com.zivlazarov.chessengine.Tile;

import java.util.ArrayList;

public class BishopPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private char name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;

    public BishopPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

        name = 'B';
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

        // TODO: use 1 loop in each iteration, maybe use a local variable outside of loop and zero it right before each one executes

        // "going right and down diagonally"
        for (int i = x + 1, j = y + 1; i < board.getBoard().length && j < board.getBoard().length; i++, j++) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }
        // "going left and up diagonally"
        for (int i = x - 1, j = y - 1; i >= 0 && j >=0; i--, j--) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }

        // "going right and up diagonally"
        for (int i = x + 1, j = y - 1; i < board.getBoard().length && j >= 0; i++, j--) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
        }

        // "going left and down diagonally"
        for (int i = x - 1, j = y + 1; i >= 0 && j < board.getBoard().length; i--, j++) {
            if (board.getBoard()[i][j].isEmpty()) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else if (board.getBoard()[i][j].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(board.getBoard()[i][j]);
            } else break;
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
            generateTilesToMoveTo();
        }
    }

    /*
    @Override
    public void checkAvailabilityAtTile(Tile tile) {
        if (tile.isEmpty()) {
            tilesToMoveTo.add(tile);
        } else if (tile.getPiece().getPieceColor() != pieceColor) {
            tilesToMoveTo.add(tile);
            break;
        } else break;
    } */
}
