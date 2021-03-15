package com.zivlazarov.chessengine.pieces;

import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.Piece;
import com.zivlazarov.chessengine.utils.PieceColor;
import com.zivlazarov.chessengine.utils.Tile;

import java.util.ArrayList;

public class KingPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private char name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;

    public KingPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

        name = 'K';
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
        // checking if King's legal game steps are possible and adding it to tilesToMoveTo
        if (x + 1 < board.getBoard().length) {
            if (board.getBoard()[x + 1][y].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x + 1][y])) {
                    tilesToMoveTo.add(board.getBoard()[x + 1][y]);
                }
            } else {
                if (board.getBoard()[x+1][y].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y])) {
                        tilesToMoveTo.add(board.getBoard()[x + 1][y]);
                    }
                }
            }
        }
        if (x - 1 >= 0) {
            if (board.getBoard()[x - 1][y].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x - 1][y])) {
                    tilesToMoveTo.add(board.getBoard()[x - 1][y]);
                }
            } else {
                if (board.getBoard()[x - 1][y].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y])) {
                        tilesToMoveTo.add(board.getBoard()[x - 1][y]);
                    }
                }
            }
        }
        if (y + 1 < board.getBoard().length) {
            if (board.getBoard()[x][y + 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x][y + 1])) {
                    tilesToMoveTo.add(board.getBoard()[x][y + 1]);
                }
            } else {
                if (board.getBoard()[x][y + 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x][y + 1])) {
                        tilesToMoveTo.add(board.getBoard()[x][y + 1]);
                    }
                }
            }
        }
        if (y - 1 >= 0) {
            if (board.getBoard()[x][y - 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x][y - 1])) {
                    tilesToMoveTo.add(board.getBoard()[x][y - 1]);
                }
            } else {
                if (board.getBoard()[x][y - 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x][y - 1])) {
                        tilesToMoveTo.add(board.getBoard()[x][y - 1]);
                    }
                }
            }
        }
        if (x + 1 < board.getBoard().length && y + 1 < board.getBoard().length) {
            if (board.getBoard()[x + 1][y + 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x + 1][y + 1])) {
                    tilesToMoveTo.add(board.getBoard()[x + 1][y + 1]);
                }
            } else {
                if (board.getBoard()[x + 1][y + 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y + 1])) {
                        tilesToMoveTo.add(board.getBoard()[x + 1][y + 1]);
                    }
                }
            }
        }
        if (x + 1 < board.getBoard().length && y - 1 >= 0) {
            if (board.getBoard()[x + 1][y - 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x + 1][y - 1])) {
                    tilesToMoveTo.add(board.getBoard()[x + 1][y - 1]);
                }
            } else {
                if (board.getBoard()[x + 1][y - 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x + 1][y - 1])) {
                        tilesToMoveTo.add(board.getBoard()[x + 1][y - 1]);
                    }
                }
            }
        }
        if (x - 1 >= 0 && y + 1 < board.getBoard().length) {
            if (board.getBoard()[x - 1][y + 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x - 1][y + 1])) {
                    tilesToMoveTo.add(board.getBoard()[x - 1][y + 1]);
                }
            } else {
                if (board.getBoard()[x - 1][y + 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y + 1])) {
                        tilesToMoveTo.add(board.getBoard()[x - 1][y + 1]);
                    }
                }
            }
        }
        if (x - 1 >= 0 && y - 1 >= 0) {
            if (board.getBoard()[x - 1][y - 1].isEmpty()) {
                if (!isThreatenedAtTile(board.getBoard()[x - 1][y - 1])) {
                    tilesToMoveTo.add(board.getBoard()[x - 1][y - 1]);
                }
            } else {
                if (board.getBoard()[x - 1][y - 1].getPiece().getPieceColor() != pieceColor) {
                    if (!isThreatenedAtTile(board.getBoard()[x - 1][y - 1])) {
                        tilesToMoveTo.add(board.getBoard()[x - 1][y - 1]);
                    }
                }
            }
        }
        /*
        for (Tile tile : tilesToMoveTo) {
            // setting every possible move for king as tile threatened by king's piece color
            if (pieceColor == PieceColor.WHITE) {
                if (tile.isThreatenedByWhite()) return; else tile.setThreatenedByWhite(true);
            } else if (pieceColor == PieceColor.BLACK) {
                if (tile.isThreatenedByBlack()) return; else tile.setThreatenedByBlack(true);
            }
        }
        */
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
