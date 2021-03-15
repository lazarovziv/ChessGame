package com.zivlazarov.chessengine.pieces;

import com.zivlazarov.chessengine.utils.Board;
import com.zivlazarov.chessengine.utils.Piece;
import com.zivlazarov.chessengine.utils.PieceColor;
import com.zivlazarov.chessengine.utils.Tile;
import javafx.scene.image.ImageView;

import java.util.ArrayList;

public class KnightPiece implements Piece {

    private final ArrayList<Tile> tilesToMoveTo;
    private final Board board;
    private char name;
    private boolean isAlive = true;
    private boolean isInDanger = false;
    private Tile currentTile;
    private PieceColor pieceColor;
    private ImageView imageIcon;

    public KnightPiece(Board board, PieceColor pc, Tile initTile) {
        this.board = board;

        name = 'N';
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
        
        // 1 right 2 up
        if (x + 2 < board.getBoard().length && y + 1 < board.getBoard().length) {
            if (isTileAvailable(board.getBoard()[x+2][y+1])) {
                tilesToMoveTo.add(board.getBoard()[x+2][y+1]);
            }
        }
        // 1 right 2 down
        if (x - 2 >= 0 && y + 1 < board.getBoard().length) {
            if (isTileAvailable(board.getBoard()[x-2][y+1])) {
                tilesToMoveTo.add(board.getBoard()[x-2][y+1]);
            }
        }
        // 1 left 2 up
        if (x + 2 < board.getBoard().length && y - 1 >= 0) {
            if (isTileAvailable(board.getBoard()[x+2][y-1])) {
                tilesToMoveTo.add(board.getBoard()[x+2][y-1]);
            }
        }
        // 1 left 2 down
        if (x - 2 >= 0 && y - 1 >= 0) {
            if (isTileAvailable(board.getBoard()[x-2][y-1])) {
                tilesToMoveTo.add(board.getBoard()[x-2][y-1]);
            }
        }
        // 2 right 1 up
        if (x + 1 < board.getBoard().length && y + 2 < board.getBoard().length) {
            if (isTileAvailable(board.getBoard()[x+1][y+2])) {
                tilesToMoveTo.add(board.getBoard()[x+1][y+2]);
            }
        }
        // 2 right 1 down
        if (x - 1 >= 0 && y + 2 < board.getBoard().length) {
            if (isTileAvailable(board.getBoard()[x-1][y+2])) {
                tilesToMoveTo.add(board.getBoard()[x-1][y+2]);
            }
        }
        // 2 left 1 up
        if (x + 1 < board.getBoard().length && y - 2 >= 0) {
            if (isTileAvailable(board.getBoard()[x+1][y-2])) {
                tilesToMoveTo.add(board.getBoard()[x+1][y-2]);
            }
        }
        // 2 left 1 down
        if (x - 1 >= 0 && y - 2 >= 0) {
            if (isTileAvailable(board.getBoard()[x-1][y-2])) {
                tilesToMoveTo.add(board.getBoard()[x-1][y-2]);
            }
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
    public ImageView getImageIcon() {
        return imageIcon;
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
    public void setImageIcon(ImageView imageIcon) {
        this.imageIcon = imageIcon;
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
            // check if tile has opponent's piece and if so, mark as not alive
            if (!tile.isEmpty()) {
                tile.getPiece().setIsAlive(false);
                board.getAlivePieces().remove(tile.getPiece());
            }
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
