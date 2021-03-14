package com.zivlazarov.chessengine.pieces;

import com.zivlazarov.chessengine.Board;
import com.zivlazarov.chessengine.Piece;
import com.zivlazarov.chessengine.PieceColor;
import com.zivlazarov.chessengine.Tile;

import java.util.ArrayList;

public class RookPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private char name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;

    public RookPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

        name = 'R';
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

        // checking the board for threats before adding moves to tilesToMoveTo
        // TODO: checking the board after every turn instead of every generation of moves to each piece to save memory
        board.checkBoard();

        Tile[] currentRow = board.getBoard()[x];
        Tile[] currentCol = new Tile[8];
        for (int i = 0; i < board.getBoard().length; i++) {
            currentCol[i] = board.getBoard()[i][y];
        }

        // TODO: add possible moves at "Check" situation

        // CHECKING ROW
        // checking if Rook is at position 0, need to check only one direction instead of 2
        if (y == 0) {
            checkTilesTowardsEndFromIndex(y, currentRow);
            // checking other way around, instead of position 0, last position 7
        } else if (y == board.getBoard().length - 1) {
            checkTilesTowardsStartFromIndex(y, currentRow);
            // checking if Rook is not on edge of any of board's rows
        } else {
            // "going right"
            checkTilesTowardsEndFromIndex(y, currentRow);
            // "going left"
            checkTilesTowardsStartFromIndex(y, currentRow);
        }


        // CHECKING COLUMN
        if (x == 0) {
            checkTilesTowardsEndFromIndex(x, currentCol);
        } else if (x == board.getBoard().length - 1) {
            checkTilesTowardsStartFromIndex(x, currentCol);
        } else {
            checkTilesTowardsEndFromIndex(x, currentCol);
            checkTilesTowardsStartFromIndex(x, currentCol);
        }
    }

    private void checkTilesTowardsEndFromIndex(int index, Tile[] tiles) {
        for (int i = index + 1; i < board.getBoard().length; i++) {
            if (tiles[i].isEmpty()) {
                tilesToMoveTo.add(tiles[i]);
                // if not empty but contains opponent's piece, can be added to tilesToMoveTo but after that need to break loop
            } else if (tiles[i].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(tiles[i]);
                break;
                // if not empty but contains same piece color, can't be moved there and must break loop
            } else break;
        }
    }

    private void checkTilesTowardsStartFromIndex(int index, Tile[] tiles) {
        for (int i = index - 1; i >= 0; i--) {
            if (tiles[i].isEmpty()) {
                tilesToMoveTo.add(tiles[i]);
                // if not empty but contains opponent's piece, can be added to tilesToMoveTo but after that need to break loop
            } else if (tiles[i].getPiece().getPieceColor() != pieceColor) {
                tilesToMoveTo.add(tiles[i]);
                break;
                // if not empty but contains same piece color, can't be moved there and must break loop
            } else break;
        }
    }

    @Override
    public char getName() {
        return name;
    }

    @Override
    public void setName(char name) {
        this.name = name;
    }

    @Override
    public boolean getIsAlive() {
        return isAlive;
    }

    @Override
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    @Override
    public boolean getIsInDanger() {
        return isInDanger;
    }

    @Override
    public void setIsInDanger(boolean isInDanger) {
        this.isInDanger = isInDanger;
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

    @Override
    public boolean isTileAvailable(Tile tile) {
        if (tile.isEmpty()) {
            return true;
        } else return tile.getPiece().getPieceColor() != pieceColor;
    }
}
