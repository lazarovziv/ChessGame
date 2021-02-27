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

        ArrayList<Tile> unoccupiedSquaresInCurrentRow = new ArrayList<Tile>();
        ArrayList<Tile> unoccupiedSquaresInCurrentCol = new ArrayList<Tile>();

        // checking one side of possible direction in row (not column!!)
        for (int r = y; r >= 0; r--) {
            // checking if Rook is at position 0, need to check one direction instead of 2
            if (r == 0) {
                for (int i = y+1; i < board.getBoard().length; i++) {
                    if (currentRow[i].isEmpty()) {
                        unoccupiedSquaresInCurrentRow.add(currentRow[i]);
                        tilesToMoveTo.add(currentRow[i]);
                        // if not empty but contains opponent's piece, can be add to tilesToMoveTo but after that need to break
                    } else if (currentRow[i].getPiece().getPieceColor() != pieceColor) {
                        tilesToMoveTo.add(currentRow[i]);
                        break;
                        // if not empty but contains same piece color, can't be moved there and break
                    } else break;
                }
                // checking other way around, instead of position 0, last position 7
            } else if (r == board.getBoard().length) {
                for (int i = y-1; i >= 0; i--) {
                    if (currentRow[i].isEmpty()) {
                        unoccupiedSquaresInCurrentRow.add(currentRow[i]);
                        tilesToMoveTo.add(currentRow[i]);
                        // if not empty but contains opponent's piece, can be add to tilesToMoveTo but after that need to break
                    } else if (currentRow[i].getPiece().getPieceColor() != pieceColor) {
                        tilesToMoveTo.add(currentRow[i]);
                        break;
                        // if not empty but contains same piece color, can't be moved there and break
                    } else break;
                }
                // checking if Rook is not on edge of any of board's rows
            } else {
                for (int i = y+1; y < board.getBoard().length; i++) {

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
        return isInDanger;
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
        return false;
    }
}
